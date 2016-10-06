package csi.fhict.org.ifindapp;

/**
 * Created by Gregory on 6-10-2016.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetProducts_JSON {

    // Receives a JSONObject and returns a list
    public List<HashMap<String,Object>> parse(JSONObject jObject){

        JSONArray jProducts = null;
        try {
            // Retrieves all the elements in the 'products' array
            jProducts = jObject.getJSONArray("Article");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Invoking getProducts with the array of json object
        // where each json object represent a product
        return getProducts(jProducts);
    }


    private List<HashMap<String, Object>> getProducts(JSONArray jProducts){
        int productCount = jProducts.length();
        List<HashMap<String, Object>> productList = new ArrayList<HashMap<String,Object>>();
        HashMap<String, Object> product = null;

        // Taking each product, parses and adds to list object
        for(int i=0; i<productCount;i++){
            try {
                // Call getProduct with country JSON object to parse the country
                product = getProduct((JSONObject)jProducts.get(i));
                productList.add(product);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return productList;
    }

    // Parsing the Product JSON object
    private HashMap<String, Object> getProduct(JSONObject jProducts){

        HashMap<String, Object> product = new HashMap<String, Object>();

        try {
            String Title = jProducts.getString("Title");
            //String Title_url = jProducts.getString("Title_url");
            String Desc = jProducts.getString("Desc");
            String flag = jProducts.getString("Img");
            String Prijs = jProducts.getString("Prijs");
            String Date = jProducts.getString("Date");
            String Loc = jProducts.getString("Loc");

            //Put it in the hashmap
            product.put("Title", Title);
            //product.put("Title_url", Title_url);
            product.put("Desc", Desc);
            product.put("flag", R.drawable.blank);
            product.put("flag_path", flag);
            product.put("Prijs", Prijs);
            product.put("Date", Date);
            product.put("Loc", Loc);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return product;
    }
}
