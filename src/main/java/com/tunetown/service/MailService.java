package com.tunetown.service;

import com.tunetown.utils.OTPUtils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailService {
    public static void sendMail(String receivedMail) {
        // Recipient's email ID needs to be mentioned.

        // Sender's email ID needs to be mentioned
        String from = "thetunetownute@gmail.com";

        // Assuming you are sending email from through gmail smtp
        String host = "smtp.gmail.com";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        ///System.getenv("EMAIL");

        // Get the Session object.// and pass username and password
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                //setup app password reference https://support.google.com/accounts/answer/185833?hl=en
                return new PasswordAuthentication(from, "wlaq niqd awcq bski");

            }

        });

        // Used to debug SMTP issues
        session.setDebug(true);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(receivedMail));

            // Set Subject: header field
            message.setSubject("Forget Password","UTF-8");

            int otp = OTPUtils.generateOTP(receivedMail);

            // Set the OTP in the message text
            String otpText = "Your OTP code to get password is: " + otp;
            message.setText(otpText);

            System.out.println("sending...");
            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}
