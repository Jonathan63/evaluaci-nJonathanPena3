import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.client.RestTemplate;

public class ejercicio3 {
    public static void main(String[] args) {
        // URL del servicio
        String serviceURL = "https://jsonplaceholder.typicode.com/posts";

        RestTemplate restTemplate = new RestTemplate();

        String response = restTemplate.getForObject(serviceURL, String.class);

        // respuesta JSON
        JSONArray jsonArray = new JSONArray(response);

        // Conectar a la base de datos H2
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "")) {
            // Crear la tabla
            String createTableSQL = "CREATE TABLE IF NOT EXISTS posts (userId INT, id INT, title VARCHAR(255), body VARCHAR(255))";
            connection.createStatement().execute(createTableSQL);

            String insertSQL = "INSERT INTO posts (userId, id, title, body) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);

            // Insertar los datos
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonPost = jsonArray.getJSONObject(i);
                int userId = jsonPost.getInt("userId");
                int id = jsonPost.getInt("id");
                String title = jsonPost.getString("title");
                String body = jsonPost.getString("body");

                preparedStatement.setInt(1, userId);
                preparedStatement.setInt(2, id);
                preparedStatement.setString(3, title);
                preparedStatement.setString(4, body);

                preparedStatement.executeUpdate();
            }

            System.out.println("Datos almacenados en la base de datos H2.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
