package com.example.econome

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.econome.data.datasource.LocalFileNoSqlDataSource
import com.example.econome.data.local.AppDatabase
import com.example.econome.data.repository.DualRepositoryImpl
import com.example.econome.domain.model.Transaction
import com.example.econome.domain.model.TransactionType
import com.example.econome.ui.theme.EconomeTheme
import com.example.econome.ui.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

// Definición de colores para uso rápido en este archivo
private val NavyBlue = Color(0xFF1A1C2E)
private val Tomato = Color(0xFFFF6347)
private val LightTomato = Color(0xFFFFEBE8)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val db = AppDatabase.getDatabase(this)
        val noSqlDataSource = LocalFileNoSqlDataSource(this)
        val repository = DualRepositoryImpl(db.transactionDao(), noSqlDataSource)
        val viewModel = TransactionViewModel(repository)

        enableEdgeToEdge()
        setContent {
            EconomeTheme {
                EconoMeApp(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EconoMeApp(viewModel: TransactionViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EconoMe Tracker", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    Switch(
                        checked = viewModel.isSqlActive,
                        onCheckedChange = { viewModel.onToggleEngine(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Tomato,
                            checkedTrackColor = Tomato.copy(alpha = 0.5f)
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavyBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    viewModel.resetForm()
                    viewModel.showAddDialog = true 
                },
                containerColor = Tomato,
                contentColor = Color.White,
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize().background(Color(0xFFF9FAFB))) {
            EngineIndicator(viewModel.isSqlActive)
            
            TabSwitcher(
                selectedTab = viewModel.currentTab,
                onTabSelected = { viewModel.currentTab = it }
            )

            if (viewModel.currentTab == 0) {
                TransactionListScreen(
                    transactions = viewModel.transactions,
                    onEdit = { viewModel.startEditing(it) },
                    onDelete = { viewModel.delete(it) }
                )
            } else {
                DashboardScreen(viewModel)
            }
        }

        if (viewModel.showAddDialog) {
            NewTransactionDialog(viewModel)
        }
    }
}

@Composable
fun EngineIndicator(isSql: Boolean) {
    Surface(
        color = Color(0xFFE5E7EB),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Engine: ${if (isSql) "SQLite (Room)" else "NoSQL (JSON Local)"}",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelMedium,
            color = NavyBlue,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun TabSwitcher(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .background(Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
            .padding(4.dp)
    ) {
        val tabs = listOf("Transacciones", "Dashboard")
        tabs.forEachIndexed { index, label ->
            Button(
                onClick = { onTabSelected(index) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == index) NavyBlue else Color.Transparent,
                    contentColor = if (selectedTab == index) Color.White else Color.Gray
                ),
                shape = RoundedCornerShape(8.dp),
                elevation = null,
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Text(label, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal)
            }
        }
    }
}

@Composable
fun TransactionListScreen(
    transactions: List<Transaction>,
    onEdit: (Transaction) -> Unit,
    onDelete: (Transaction) -> Unit
) {
    if (transactions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay transacciones registradas", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(transactions) { tx ->
                TransactionCard(tx, onEdit, onDelete)
            }
        }
    }
}

@Composable
fun TransactionCard(
    tx: Transaction,
    onEdit: (Transaction) -> Unit,
    onDelete: (Transaction) -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(tx.concept, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NavyBlue)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Text(dateFormat.format(Date(tx.date)), color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = Color(0xFFF3F4F6),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            tx.category,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontSize = 12.sp,
                            color = NavyBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                val color = if (tx.type == TransactionType.INCOME) Color(0xFF10B981) else Tomato
                val prefix = if (tx.type == TransactionType.INCOME) "+" else "-"
                Text(
                    "$prefix$${"%.2f".format(tx.amount)}",
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                IconButton(onClick = { onEdit(tx) }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = NavyBlue.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = { onDelete(tx) }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Tomato.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun DashboardScreen(viewModel: TransactionViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryCard("Ingresos", viewModel.totalIncome, Color(0xFF10B981), Modifier.weight(1f))
            SummaryCard("Gastos", viewModel.totalExpenses, Tomato, Modifier.weight(1f))
        }
        SummaryCard("Balance Total", viewModel.balance, if(viewModel.balance >= 0) NavyBlue else Tomato, Modifier.fillMaxWidth())

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Tendencia Financiera", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NavyBlue)
                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color(0xFFF9FAFB), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Visualización de Tendencia", color = Color.LightGray)
                }
            }
        }
    }
}

@Composable
fun SummaryCard(label: String, amount: Double, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text("$${"%.2f".format(amount)}", color = color, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        }
    }
}

@Composable
fun NewTransactionDialog(viewModel: TransactionViewModel) {
    Dialog(onDismissRequest = { 
        viewModel.showAddDialog = false
        viewModel.resetForm()
    }) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (viewModel.editingTransaction == null) "Nueva Transacción" else "Editar Transacción",
                        fontWeight = FontWeight.Bold, 
                        fontSize = 22.sp,
                        color = NavyBlue
                    )
                    IconButton(onClick = { 
                        viewModel.showAddDialog = false 
                        viewModel.resetForm()
                    }) {
                        Text("✕", fontSize = 20.sp, color = NavyBlue, fontWeight = FontWeight.Bold)
                    }
                }
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Tipo", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyBlue)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        FilterChip(
                            selected = viewModel.selectedType == TransactionType.INCOME,
                            onClick = { viewModel.selectedType = TransactionType.INCOME },
                            label = { Text("Ingreso") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF10B981),
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = viewModel.selectedType == TransactionType.EXPENSE,
                            onClick = { viewModel.selectedType = TransactionType.EXPENSE },
                            label = { Text("Gasto") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Tomato,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = viewModel.concept,
                    onValueChange = { viewModel.concept = it },
                    label = { Text("Concepto") },
                    placeholder = { Text("Ej: Supermercado, Salario...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NavyBlue,
                        focusedLabelColor = NavyBlue
                    )
                )

                OutlinedTextField(
                    value = viewModel.amount,
                    onValueChange = { viewModel.amount = it },
                    label = { Text("Monto") },
                    placeholder = { Text("0.00") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NavyBlue,
                        focusedLabelColor = NavyBlue
                    )
                )

                var expanded by remember { mutableStateOf(false) }
                Box {
                    OutlinedTextField(
                        value = viewModel.selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            IconButton(onClick = { expanded = true }) {
                                Text("▾", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NavyBlue,
                            focusedLabelColor = NavyBlue
                        )
                    )
                    DropdownMenu(
                        expanded = expanded, 
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        listOf("Alimento", "Servicios", "Transporte", "Otros").forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    viewModel.selectedCategory = cat
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.save() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text(
                        if (viewModel.editingTransaction == null) "GUARDAR" else "ACTUALIZAR", 
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}
