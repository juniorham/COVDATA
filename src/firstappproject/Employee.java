/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package firstappproject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JOptionPane;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author Malx
 */
public class Employee {
    private Statement stmt;
    private ResultSet rs;
    
    public Employee(Statement statement) {
        this.stmt = statement;
    }
    
    public boolean login(String userName, String password){
        
        try{
            rs = stmt.executeQuery("SELECT username, password from users where username = '"+userName+"' and password='"+password+"'");

            if(!rs.next()){
                return false;
            } else {
                return true;
            }
        } catch(Exception e){

            System.out.print(e);
        }

        return false;
    }
    
    public boolean addUser(String username, String password){

        String query = "INSERT into users(username, password) VALUES ('"+username+"', '"+password+"')";
        try{
            int x = stmt.executeUpdate(query);
            
            if(x > 0)
                return true;

        } catch (Exception e){
            System.out.println(e);
        }
        return false;
    }
    
    public void sendEmail(String toAddress,String subject, String message) throws AddressException, MessagingException, IOException {
        Properties defaultProps = new Properties();
        // sets default properties
        defaultProps.setProperty("mail.smtp.host", "smtp.gmail.com");
        defaultProps.setProperty("mail.smtp.port", "587");
        defaultProps.setProperty("mail.user", "getbettermeds@gmail.com");
        defaultProps.setProperty("mail.password", "GetBetter_12");
        defaultProps.setProperty("mail.smtp.starttls.enable", "true");
        defaultProps.setProperty("mail.smtp.auth", "true");
        defaultProps.setProperty("mail.smtp.ssl.trust", "smtp.gmail.com");
    
        final String userName = defaultProps.getProperty("mail.user");
        final String password = defaultProps.getProperty("mail.password");
            
        // creates a new session with an authenticator
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        };
        Session session = Session.getInstance(defaultProps, auth);
    
        // creates a new e-mail message
        Message msg = new MimeMessage(session);
    
        msg.setFrom(new InternetAddress(userName));
        InternetAddress[] toAddresses = { new InternetAddress(toAddress) };
        msg.setRecipients(Message.RecipientType.TO, toAddresses);
        msg.setSubject(subject);
        msg.setSentDate(new java.util.Date());
    
        // creates message part
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(message, "text/html");
    
        // creates multi-part
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
    
        // sets the multi-part as e-mail's content
        msg.setContent(multipart);
    
        // sends the e-mail
        Transport.send(msg);
    }
    
    public String[] covidCases(String country, javax.swing.JLabel countryCasesSearchLabel){
        String[] results = new String[4];
        countryCasesSearchLabel.setText("Searching...");
        try{
            URL url = new URL("https://disease.sh/v3/covid-19/countries/" + country);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            //Check if connect is made
            int responseCode = conn.getResponseCode();

            if(responseCode != 200){
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {
                StringBuilder informationString = new StringBuilder();
                Scanner scanner = new Scanner(url.openStream());

                while(scanner.hasNext()){
                    informationString.append(scanner.nextLine());
                }

                scanner.close();

                //System.out.println(informationString);
                String stringcountrydata = informationString.toString();
                Object file = JSONValue.parse(stringcountrydata);
                JSONObject jsonObjectdecode = (JSONObject) file;

                String covidDeaths = String.valueOf(jsonObjectdecode.get("deaths"));
                String covidRecoveries = String.valueOf(jsonObjectdecode.get("recovered"));
                String activeCases = String.valueOf(jsonObjectdecode.get("active"));

                System.out.println("Country: " + country);
                System.out.println("Active cases: " + activeCases);
                System.out.println("Recoveries: " + covidRecoveries);
                System.out.println("Deaths: " + covidDeaths);
                results[0] = covidDeaths;
                results[1] = activeCases;
                results[2] = covidDeaths;
                
            }

        }catch(Exception e){
            System.out.println(e);
            JOptionPane.showMessageDialog(null, e);
        }
        
        return results;
    }
    
}
