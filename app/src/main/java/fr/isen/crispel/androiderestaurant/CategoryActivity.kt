package fr.isen.crispel.androiderestaurant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import fr.isen.crispel.androiderestaurant.ui.theme.AndroidERestaurantTheme
import org.json.JSONObject

class CategoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val categoryName = intent.getStringExtra("categoryName")
        val backgroundColor = intent.getStringExtra("backgroundColor")
        val contentColor = intent.getStringExtra("contentColor")


        apiRequest(categoryName.toString()) { response ->
            val filteredResponse = filterResponseByCategory(categoryName.toString(), response)
            setContent {
                AndroidERestaurantTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        CategoryComponent(
                            filteredResponse,
                            categoryName = categoryName ?: "Category",
                            backgroundColorString = backgroundColor ?: "#FFFFFFFF",
                            contentColorString = contentColor ?: "#FF000000"
                        )
                    }
                }
            }
        }
    }

    private fun apiRequest(categoryName: String, callback: (JsonObject) -> Unit) {
        val queue = Volley.newRequestQueue(this)
        val url = "http://test.api.catering.bluecodegames.com/menu"

        val params = JSONObject()
        params.put("id_shop", "1")

        val jsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            url,
            params,
            Response.Listener<JSONObject> { response ->
                // Handling Success
                Log.d("Success", "simpleRequest:$response")
                val gson = Gson()
                val data: JsonObject = gson.fromJson(response.toString(), JsonObject::class.java)
                callback(data)
            },
            Response.ErrorListener { error ->
                // Handling Error
                Log.d("Error", "simpleRequest:$error")
                callback(JsonObject()) // You need to handle this part according to your app logic
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json; charset=utf-8"
                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }

    private fun filterResponseByCategory(categoryName: String, response: JsonObject): JsonObject {
        val data = response.getAsJsonArray("data") ?: return JsonObject()

        return when (categoryName) {
            "Starters" -> data.get(0).asJsonObject
            "Main Courses" -> data.get(1).asJsonObject
            "Desserts" -> data.get(2).asJsonObject
            else -> JsonObject()
        }
    }
}

@Composable
fun getContainerColorFromCategoryName(categoryName: String): Color {
    return when (categoryName) {
        "Starters" -> MaterialTheme.colorScheme.primaryContainer
        "Main Courses" -> MaterialTheme.colorScheme.secondaryContainer
        "Desserts" -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.primaryContainer
    }
}

@Composable
fun getOnContainerColorFromCategoryName(categoryName: String): Color {
    return when (categoryName) {
        "Starters" -> MaterialTheme.colorScheme.onPrimaryContainer
        "Main Courses" -> MaterialTheme.colorScheme.onSecondaryContainer
        "Desserts" -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onPrimaryContainer
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryComponent(response: JsonObject, categoryName: String, backgroundColorString: String, contentColorString: String) {
    val backgroundColor = stringToColor(backgroundColorString)
    val contentColor = stringToColor(contentColorString)
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(categoryName)
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
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .padding(0.dp, 8.dp)
                .padding(bottom = 72.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            val items = response.getAsJsonArray("items")
            for (item in items) {
                val itemObject = item.asJsonObject
                // log the type of itemObject
                Log.d("itemObject type", itemObject::class.java.simpleName)
                val name = itemObject.get("name_fr").asString
                val imageUrl = itemObject.getAsJsonArray("images").get(0).asString
                val price = itemObject.getAsJsonArray("prices").get(0).asJsonObject.get("price").asString

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp, 8.dp)
                            .clip(RoundedCornerShape(25.dp))
                            .background(backgroundColor)
                            .padding(10.dp)
                    ) {
                        Column {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16f / 9f)
                                    .padding(bottom = 8.dp)
                                    .clip(RoundedCornerShape(15.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Row {
                                Text(
                                    text = name,
                                    color = contentColor,
                                    style = TextStyle(fontSize = 20.sp),
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp)
                                        .weight(2f)
                                )
                                DisplayDetailsButton(categoryName, price, item, backgroundColor, contentColor)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DisplayDetailsButton(categoryName: String, price: String, item: JsonElement, backgroundColor: Color, contentColor: Color) {
    val context = LocalContext.current
    Button(
        onClick = {
            val intent = Intent(context, MealDetailsActivity::class.java)
            intent.putExtra("item", item.toString())
            intent.putExtra("backgroundColor", backgroundColor.toString())
            intent.putExtra("contentColor", contentColor.toString())
            context.startActivity(intent)
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = getContainerColorFromCategoryName(categoryName),
            contentColor = getOnContainerColorFromCategoryName(categoryName)
        ),
        modifier = Modifier
            .padding(start = 4.dp)
    ) {
        Text(
            text = "Add! $price€",
            style = TextStyle(fontSize = 16.sp),
        )
    }
}

fun stringToColor(color: String): Color {
    val colorValues = color
        .removePrefix("Color(")
        .removeSuffix(")")
        .split(", ")
    val colorR = (colorValues[0].toDouble() * 255).toInt()
    val colorG = (colorValues[1].toDouble() * 255).toInt()
    val colorB = (colorValues[2].toDouble() * 255).toInt()
    val colorA = (colorValues[3].toDouble() * 255).toInt()
    return Color(android.graphics.Color.argb(colorA, colorR, colorG, colorB))
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    AndroidERestaurantTheme {
        //Greeting2("Android")
    }
}