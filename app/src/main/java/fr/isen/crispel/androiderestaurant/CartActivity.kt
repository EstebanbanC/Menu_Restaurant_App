package fr.isen.crispel.androiderestaurant

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import fr.isen.crispel.androiderestaurant.ui.theme.AndroidERestaurantTheme
import java.io.File

class CartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidERestaurantTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CartComponent()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartComponent() {
    val context = LocalContext.current
    val file = File(context.filesDir, "cart.json")
    val cartState = remember { mutableStateOf(if (file.exists()) JsonParser.parseString(file.readText()).asJsonArray else JsonArray()) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(stringResource(id = R.string.title_activity_cart))
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        emptyCart(context, cartState)
                    },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Order now!",
                    style = TextStyle(fontSize = 30.sp)
                )
            }
        },
    ) { innerPadding ->
        if (cartState.value.size() > 0) {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                for (itemInfos in cartState.value) {
                    val itemObject =
                        JsonParser.parseString(itemInfos.asJsonObject.get("item").asString).asJsonObject
                    val quantity = itemInfos.asJsonObject.get("quantity").asDouble
                    val imageUrl = itemObject.getAsJsonArray("images").get(0).asString
                    val itemName = itemObject.get("name_en").asString
                    val categoryName = itemObject.get("categ_name_en").asString

                    item {
                        val color = getColorFromCategoryName(categoryName)
                        val onColor = getOnColorFromCategoryName(categoryName)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp, 4.dp)
                                .clip(RoundedCornerShape(25.dp))
                                .background(color)
                                .padding(10.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center
                            ) {
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f / 1f)
                                        .clip(RoundedCornerShape(15.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Text(
                                    text = itemName,
                                    modifier = Modifier
                                        .weight(4f)
                                        .padding(start=8.dp),
                                    color = onColor,
                                    style = TextStyle(fontSize = 20.sp)
                                )
                                Text(
                                    text = quantity.toInt().toString(),
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start=8.dp),
                                    color = onColor,
                                    style = TextStyle(fontSize = 20.sp)
                                )
                                IconButton(
                                    onClick = {
                                        val cartList = cartState.value.toMutableList()
                                        val itemIndex = cartList.indexOf(itemInfos)

                                        cartList.removeAt(itemIndex)
                                        val updatedCart = JsonArray()
                                        cartList.forEach { updatedCart.add(it) }
                                        file.writeText(updatedCart.toString())

                                        cartState.value = updatedCart
                                    }
                                ) {
                                    Icon(
                                        Icons.Filled.Delete,
                                        modifier = Modifier
                                            .weight(1f),
                                        contentDescription = "Delete item",
                                        tint = onColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Empty cart!",
                    style = TextStyle(fontSize = 30.sp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

fun emptyCart(context: Context, cartState: MutableState<JsonArray>) {
    val file = File(context.filesDir, "cart.json")
    file.writeText(JsonArray().toString())
    updateCartCounter(context, 0)
    cartState.value = JsonArray()
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview4() {
    AndroidERestaurantTheme {
    }
}