package fr.isen.crispel.androiderestaurant

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.crispel.androiderestaurant.ui.theme.AndroidERestaurantTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidERestaurantTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeComponent()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeComponent() {
    val context = LocalContext.current
    val cartCount = remember { mutableIntStateOf(getCartCounter(context)) }

    LaunchedEffect(key1 = true) {
        cartCount.intValue = getCartCounter(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(stringResource(id = R.string.app_name))
                }
            )
        },
        floatingActionButton = {
            BadgedBox(
                badge = {
                    Badge(
                        modifier = Modifier.offset(x = (-8).dp, y = 8.dp)
                    ) {
                        Text(cartCount.intValue.toString())
                    }
                }
            ) {
                FloatingActionButton(
                    onClick = {
                        val intent = Intent(context, CartActivity::class.java)
                        context.startActivity(intent)
                    }
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth(),
                painter = painterResource(id = if (isSystemInDarkTheme()) R.drawable.logolight else R.drawable.logodark),
                contentDescription = "logo",
            )
            DisplayCategoryButton(
                name = stringResource(id = R.string.starters),
                backgroundColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
            DisplayCategoryButton(
                name = stringResource(id = R.string.main_courses),
                backgroundColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
            DisplayCategoryButton(
                name = stringResource(id = R.string.desserts),
                backgroundColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary
            )
        }
    }
}

@Composable
fun DisplayCategoryButton(name: String, backgroundColor: Color, contentColor: Color) {
    val context = LocalContext.current
    Button(
        modifier = Modifier
            .padding(16.dp, 0.dp)
            .width(250.dp),
        onClick = {
            val intent = Intent(context, CategoryActivity::class.java)
            intent.putExtra("categoryName", name)
            intent.putExtra("backgroundColor", backgroundColor.toString())
            intent.putExtra("contentColor", contentColor.toString())
            context.startActivity(intent)
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        )
    ) {
        Text(
            name,
            style = TextStyle(fontSize = 25.sp),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidERestaurantTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column {
            }
        }
    }
}