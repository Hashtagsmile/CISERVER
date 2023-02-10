import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.util.HashMap;
import java.util.Properties;

public class Features {

    /**
     * Executes maven commands for installing and compiling the cloned repository.
     * Flags are used to check if the commands was successful.
     * @param map A hashmap with JSON object values
     * @param repo The name of the repo
     * @return map The hashmap with updated Status value
     */
    public static HashMap<String,String> compileRepo(HashMap<String,String> map, String repo) throws IOException {

        //Compile
        Process compileProcess = Runtime.getRuntime().exec("mvn compile", null, new File(repo));
        try {
            compileProcess.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(compileProcess.getInputStream()));
        String line = "";
        boolean compileFlag = false;
        while ((line = reader.readLine()) != null) {
            if(line.contains("BUILD SUCCESS")){
                compileFlag = true;
                map.put("Status","Success");
                break;
            }
        }
        if (!compileFlag) {
            map.put("Status", "Error");
        }
        return map;
    }


    /**
     * This method runs the maven tests and sets a testflag.
     * @param repo The URL of the repo
     * @return testFlag Boolean to indicate success or failure
     */
    public static boolean testRepo(String repo) throws IOException {
        boolean testFlag = false;
        Process testProcess = Runtime.getRuntime().exec("mvn test", null, new File(repo));
        try {
            testProcess.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String line = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(testProcess.getInputStream()));
        while ((line = reader.readLine()) != null) {
            if(line.contains("BUILD SUCCESS")){
                testFlag = true;
                break;
            }
            if(line.contains("BUILD FAILURE")){
                break;
            }
        }
        return testFlag;
    }

    /**
     * This method sends an email to the members of the repository
     * with information about the latest push.
     * @param map jsonInfo The hashMap containing information about the push
     * @return void
     */
    public static void sendNotificationMail(HashMap<String,String> jsonInfo){
        String username = "group8dd2480@gmail.com";
        String password = "zord ozat wont oqtf";

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS

        Session session = Session.getDefaultInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            String committer = jsonInfo.get("Author");
            String commitId = jsonInfo.get("Head_Id");
            String branch = jsonInfo.get("BranchName");
            String timestamp = jsonInfo.get("Timestamp");
            String compStatus = jsonInfo.get("Status");
            String testStatus = jsonInfo.get("TestStatus");

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("group8dd2480@gmail.com"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse("rabihanna007@gmail.com, miltonlindblad@gmail.com, hastimazadeh@gmail.com, alex.binett@hotmail.com")
            );
            message.setSubject("Push Status");
            message.setText("Committer : " + committer + "\n" + "CommitId : " + commitId + "\n" + "Timestamp : " +timestamp + "\n" +"Branch : " + branch + "\n" + "Compile Status : " + compStatus + "\n" + "Test status : " + testStatus);
            Transport.send(message);
            System.out.println("Notification sent");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
