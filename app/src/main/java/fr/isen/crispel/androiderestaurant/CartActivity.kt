package fr.isen.crispel.androiderestaurant

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    val cart = if (file.exists()) JsonParser.parseString(file.readText()).asJsonArray else JsonArray()
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
    ) { innerPadding ->
        if (cart.size() > 0) {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                for (itemInfos in cart) {
                    //item : {"item":"{\"id\":\"127\",\"name_fr\":\"Salade César\",\"name_en\":\"Salade César\",\"id_category\":\"27\",\"categ_name_fr\":\"Entrées\",\"categ_name_en\":\"Entrées\",\"images\":[\"https://www.atelierdeschefs.com/media/recette-e12196-salade-caesar-au-poulet-grille.jpg\",\"https://static.cuisineaz.com/400x320/i135580-salade-cesar-allegee.jpeg\",\"https://img.cuisineaz.com/660x660/2013-06-18/i29881-salade-cesar.jpg\"],\"ingredients\":[{\"id\":\"150\",\"id_shop\":\"1\",\"name_fr\":\"laitue\",\"name_en\":\"laitue\",\"create_date\":\"2021-01-18 21:42:35\",\"update_date\":\"2021-01-18 21:42:35\",\"id_pizza\":\"127\"},{\"id\":\"38\",\"id_shop\":\"1\",\"name_fr\":\"parmesan\",\"name_en\":\"Parmesan cheese\",\"create_date\":\"2017-07-03 22:27:17\",\"update_date\":\"2017-07-03 22:27:17\",\"id_pizza\":\"127\"},{\"id\":\"149\",\"id_shop\":\"1\",\"name_fr\":\"huile\",\"name_en\":\"huile\",\"create_date\":\"2021-01-18 21:40:09\",\"update_date\":\"2021-01-18 21:40:09\",\"id_pizza\":\"127\"},{\"id\":\"32\",\"id_shop\":\"1\",\"name_fr\":\"oeuf\",\"name_en\":\"egg\",\"create_date\":\"2017-07-03 22:19:22\",\"update_date\":\"2017-07-03 22:19:22\",\"id_pizza\":\"127\"},{\"id\":\"151\",\"id_shop\":\"1\",\"name_fr\":\"crouton\",\"name_en\":\"crouton\",\"create_date\":\"2021-01-18 21:43:19\",\"update_date\":\"2021-01-18 21:43:19\",\"id_pizza\":\"127\"},{\"id\":\"145\",\"id_shop\":\"1\",\"name_fr\":\"moutarde\",\"name_en\":\"moutarde\",\"create_date\":\"2021-01-18 21:39:18\",\"update_date\":\"2021-01-18 21:39:18\",\"id_pizza\":\"127\"}],\"prices\":[{\"id\":\"263\",\"id_pizza\":\"127\",\"id_size\":\"1\",\"price\":\"11\",\"create_date\":\"2021-01-23 17:53:25\",\"update_date\":\"2021-01-23 17:53:25\",\"size\":\"Petite\"}]}","quantity":2.0}
                    val itemObject = JsonParser.parseString(itemInfos.asJsonObject.get("item").asString).asJsonObject
                    //itemObject : {\"id\":\"127\",\"name_fr\":\"Salade César\",\"name_en\":\"Salade César\",\"id_category\":\"27\",\"categ_name_fr\":\"Entrées\",\"categ_name_en\":\"Entrées\",\"images\":[\"https://www.atelierdeschefs.com/media/recette-e12196-salade-caesar-au-poulet-grille.jpg\",\"https://static.cuisineaz.com/400x320/i135580-salade-cesar-allegee.jpeg\",\"https://img.cuisineaz.com/660x660/2013-06-18/i29881-salade-cesar.jpg\"],\"ingredients\":[{\"id\":\"150\",\"id_shop\":\"1\",\"name_fr\":\"laitue\",\"name_en\":\"laitue\",\"create_date\":\"2021-01-18 21:42:35\",\"update_date\":\"2021-01-18 21:42:35\",\"id_pizza\":\"127\"},{\"id\":\"38\",\"id_shop\":\"1\",\"name_fr\":\"parmesan\",\"name_en\":\"Parmesan cheese\",\"create_date\":\"2017-07-03 22:27:17\",\"update_date\":\"2017-07-03 22:27:17\",\"id_pizza\":\"127\"},{\"id\":\"149\",\"id_shop\":\"1\",\"name_fr\":\"huile\",\"name_en\":\"huile\",\"create_date\":\"2021-01-18 21:40:09\",\"update_date\":\"2021-01-18 21:40:09\",\"id_pizza\":\"127\"},{\"id\":\"32\",\"id_shop\":\"1\",\"name_fr\":\"oeuf\",\"name_en\":\"egg\",\"create_date\":\"2017-07-03 22:19:22\",\"update_date\":\"2017-07-03 22:19:22\",\"id_pizza\":\"127\"},{\"id\":\"151\",\"id_shop\":\"1\",\"name_fr\":\"crouton\",\"name_en\":\"crouton\",\"create_date\":\"2021-01-18 21:43:19\",\"update_date\":\"2021-01-18 21:43:19\",\"id_pizza\":\"127\"},{\"id\":\"145\",\"id_shop\":\"1\",\"name_fr\":\"moutarde\",\"name_en\":\"moutarde\",\"create_date\":\"2021-01-18 21:39:18\",\"update_date\":\"2021-01-18 21:39:18\",\"id_pizza\":\"127\"}],\"prices\":[{\"id\":\"263\",\"id_pizza\":\"127\",\"id_size\":\"1\",\"price\":\"11\",\"create_date\":\"2021-01-23 17:53:25\",\"update_date\":\"2021-01-23 17:53:25\",\"size\":\"Petite\"}]}
                    val quantity = itemInfos.asJsonObject.get("quantity").asDouble
                    val imageUrl = itemObject.getAsJsonArray("images").get(0).asString
                    val itemName = itemObject.get("name_en").asString

                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp, 8.dp)
                                .clip(RoundedCornerShape(25.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(10.dp)
                        ) {
                            Row {
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
                                    text = "test",
                                    modifier = Modifier
                                        .weight(2f)
                                )
                                Text(
                                    text = "Quantity: $quantity",
                                    modifier = Modifier
                                        .weight(2f)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Text(text = "Empty cart!")
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview4() {
    AndroidERestaurantTheme {
        Greeting("Android")
    }
}