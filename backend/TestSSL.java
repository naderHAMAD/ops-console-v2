import java.net.URI;
public class TestSSL {
    public static void main(String[] args) throws Exception {
        var url = URI.create("https://repo.maven.apache.org/maven2/").toURL();
        var connection = url.openConnection();
        connection.connect();
        System.out.println("Connexion HTTPS r?ussie : " + connection.getURL());
    }
}
