package fr.isen.crispel.androiderestaurant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import fr.isen.crispel.androiderestaurant.ui.theme.AndroidERestaurantTheme
import com.google.gson.JsonParser.parseString
import kotlinx.coroutines.launch
import java.io.File

class MealDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val item = intent.getStringExtra("item")
        val backgroundColor = intent.getStringExtra("backgroundColor",)
        val contentColor = intent.getStringExtra("contentColor",)

        setContent {
            AndroidERestaurantTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DetailsComponent(
                        item = item,
                        backgroundColor = backgroundColor,
                        contentColor = contentColor,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsComponent(item: String?, backgroundColor: String?, contentColor: String?) {
    val itemObject = parseString(item).asJsonObject
    val name = itemObject.get("name_fr").asString
    val imageUrl = itemObject.getAsJsonArray("images").get(0).asString
    val ingredients = itemObject.getAsJsonArray("ingredients")
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(name)
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val intent = Intent(context, CartActivity::class.java)
                    context.startActivity(intent)
                }
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .aspectRatio(16f / 9f),
                contentScale = ContentScale.Crop
            )
            Text(
                modifier = Modifier
                    .padding(start = 16.dp),
                style = TextStyle(fontSize = 20.sp),
                textAlign = TextAlign.Start,
                text = "Ingredients:"
            )
            for (ingredient in ingredients) {
                val ingredientName = ingredient.asJsonObject.get("name_fr").asString
                Text(
                    modifier = Modifier
                        .padding(start = 16.dp),
                    style = TextStyle(fontSize = 16.sp),
                    textAlign = TextAlign.Start,
                    text = "• $ingredientName"
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            QuantitySelector(item, backgroundColor, contentColor)
        }
    }
}

@Composable
fun QuantitySelector(item: String?, backgroundColorString: String?, contentColorString: String?) {
    val itemObject = parseString(item).asJsonObject
    val price = itemObject.getAsJsonArray("prices").get(0).asJsonObject.get("price").asString
    val counter = remember { mutableFloatStateOf(0F) }
    val backgroundColor = stringToColor(backgroundColorString.toString())
    val contentColor = stringToColor(contentColorString.toString())
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(bottom = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SnackbarHost(snackbarHostState)

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                onClick = { if (counter.floatValue > 0) counter.floatValue-- },
                modifier = Modifier
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = backgroundColor,
                    contentColor = contentColor
                )
            ) {
                Text(
                    "-",
                    style = TextStyle(fontSize = 20.sp)
                )
            }
            Text(
                text = counter.floatValue.toString() + " (" + (counter.floatValue*price.toFloat()).toString() + "€)",
                modifier = Modifier
                    .weight(3f),
                style = TextStyle(fontSize = 20.sp),
                textAlign = TextAlign.Center
            )
            Button(
                onClick = { counter.floatValue++ },
                modifier = Modifier
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = backgroundColor,
                    contentColor = contentColor
                )
            ) {
                Text(
                    "+",
                    style = TextStyle(fontSize = 20.sp)
                )
            }
        }
        Button(
            onClick = {
                if (counter.floatValue > 0) {
                    val itemJson = JsonObject()
                    itemJson.addProperty("item", item)
                    itemJson.addProperty("quantity", counter.floatValue)

                    val file = File(context.filesDir, "cart.json")
                    val existingCart = if (file.exists()) JsonParser.parseString(file.readText()).asJsonArray else JsonArray()

                    existingCart.add(itemJson)

                    file.writeText(existingCart.toString())

                    counter.floatValue = 0F

                    scope.launch {
                        snackbarHostState.showSnackbar("Item added to cart!")
                    }
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar("Please select a quantity!")
                    }
                }

            },
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                contentColor = contentColor
            )
        ) {
            Text(
                text = "Add to cart! ",
                style = TextStyle(fontSize = 20.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    AndroidERestaurantTheme {
    }
}