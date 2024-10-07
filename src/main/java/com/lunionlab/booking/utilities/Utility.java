package com.lunionlab.booking.utilities;

import java.time.temporal.TemporalUnit;
import java.util.Base64;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.util.ResourceUtils;

import com.lunionlab.booking.models.ProfileModel;
import com.lunionlab.booking.services.ProfileService;
import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Utility {

    private static ProfileService profileService;

    public static Date dateFromInteger(Integer number, TemporalUnit unit) {
        Date now = new Date();
        Date date = Date
                .from(now.toInstant().plus(number, unit));
        return date;
    }

    public static String formatDate(Date date, String format) {
        // pour l'heure hh:mm:ss
        // format = "dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    public static Date dateFromString(String date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date d = dateFormat.parse(date);
            return d;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String hashPassword(String password) {
        String salt = BCrypt.gensalt();
        String passwordHash = BCrypt.hashpw(password, salt);
        return passwordHash;
    }

    public static boolean sendMailWithResend(String from, String to, String subject, String code, String link,
            String imageLink) {
        Resend resend = new Resend("re_KJJw4scL_EcdyogNFyz2C6oz9stqvUfjs");
        CreateEmailOptions params = CreateEmailOptions.builder().from(from).to(to).subject(subject)
                .html(emailTemplate(code, link, imageLink)).build();
        try {
            CreateEmailResponse data = resend.emails().send(params);
            System.out.println(data);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static String emailTemplate(String code, String link, String imageLink) {
        Date now = new Date();
        String output = "<!DOCTYPE html>\n" + //
                "<html lang=\"fr\">\n" + //
                "<head>\n" + //
                "    <meta charset=\"UTF-8\">\n" + //
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" + //
                "    <title>Lunion-Booking Email Verification</title>\n" + //
                "    <style>\n" + //
                "        body {\n" + //
                "            font-family: 'Arial', sans-serif;\n" + //
                "            background-color: #f0f0f0;\n" + //
                "            margin: 0;\n" + //
                "            padding: 20px;\n" + //
                "            color: #333;\n" + //
                "        }\n" + //
                "        .container {\n" + //
                "            max-width: 600px;\n" + //
                "            margin: 0 auto;\n" + //
                "            background-color: white;\n" + //
                "            border-radius: 15px;\n" + //
                "            overflow: hidden;\n" + //
                "            box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);\n" + //
                "        }\n" + //
                "        .header {\n" + //
                "            background-color: hsl(348, 77%, 58%);\n" + //
                "            color: white;\n" + //
                "            padding: 30px;\n" + //
                "            text-align: center;\n" + //
                "        }\n" + //
                "        .logo {\n" + //
                "            width: 120px;\n" + //
                "            margin-bottom: 20px;\n" + //
                "        }\n" + //
                "        .content {\n" + //
                "            padding: 40px;\n" + //
                "        }\n" + //
                "        h1 {\n" + //
                "            color: hsl(348, 77%, 58%);\n" + //
                "            margin-bottom: 20px;\n" + //
                "            font-size: 24px;\n" + //
                "        }\n" + //
                "        .code {\n" + //
                "            font-size: 36px;\n" + //
                "            font-weight: bold;\n" + //
                "            color: #333;\n" + //
                "            background-color: #f8f8f8;\n" + //
                "            padding: 15px;\n" + //
                "            border-radius: 10px;\n" + //
                "            margin: 20px 0;\n" + //
                "            letter-spacing: 5px;\n" + //
                "        }\n" + //
                "        .footer {\n" + //
                "            background-color: #f8f8f8;\n" + //
                "            padding: 20px;\n" + //
                "            text-align: center;\n" + //
                "            font-size: 14px;\n" + //
                "            color: #666;\n" + //
                "        }\n" + //
                "        .social-icons {\n" + //
                "            margin-top: 20px;\n" + //
                "        }\n" + //
                "        .social-icons a {\n" + //
                "            display: inline-block;\n" + //
                "            margin: 0 10px;\n" + //
                "            color: hsl(348, 77%, 58%);\n" + //
                "            text-decoration: none;\n" + //
                "            font-weight: bold;\n" + //
                "        }\n" + //
                ".ii a[href] {\n" + //
                "    color: white;\n" + //
                "}" +
                "        .button {\n" + //
                "            display: inline-block;\n" + //
                "            background-color: hsl(348, 77%, 58%);\n" + //
                "            color: white;\n" + //
                "            padding: 12px 24px;\n" + //
                "            text-decoration: none;\n" + //
                "            border-radius: 25px;\n" + //
                "            font-weight: bold;\n" + //
                "            margin-top: 20px;\n" + //
                "            color: #FFFF important;\n" + //
                "            transition: background-color 0.3s ease;\n" + //
                "        }\n" + //
                "        .button:hover {\n" + //
                "            background-color: hsl(348, 77%, 48%);\n" + //
                "        }\n" + //
                "    </style>\n" + //
                "</head>\n" + //
                "<body>\n" + //
                "    <div class=\"container\">\n" + //
                "        <div class=\"header\">\n" + //
                "<img src='" + imageLink + "' alt=\"Lunion-Lab Logo\" class=\"logo\"> \n"
                + //
                "            <h1>Vérification de votre compte</h1>\n" + //
                "        </div>\n" + //
                "        <div class=\"content\">\n" + //
                "            <p>Bonjour,</p>\n" + //
                "            <p>Bienvenue chez Lunion-Booking. Pour sécuriser votre compte et accéder à nos services immobiliers exclusifs, veuillez utiliser le code de vérification suivant :</p>\n"
                + //
                "            <div class=\"code\">" + code + "</div>\n" + //
                "            <p>Ce code est valable pendant 5 minutes. Si vous n'avez pas demandé cette vérification, veuillez ignorer cet e-mail.</p>\n"
                + //
                "            <a href=\"" + link + "\" class=\"button\" style=\"color:white;\">Vérifier mon compte</a>\n"
                + //
                "        </div>\n" + //
                "        <div class=\"footer\">\n" + //
                "            <p>Si vous avez des questions, n'hésitez pas à contacter notre équipe support à <a href=\"mailto:lunionlab@gmail.com\">lunionlab@gmail.com</a></p>\n"
                + //
                "            <div class=\"social-icons\">\n" + //
                "                <a href=\"https://facebook.com/lunionlab\">Facebook</a>\n" + //
                "                   <a href=\"https://instagram.com/lunionlab\">instagram</a>\n" + //
                "                <a href=\"https://linkedin.com/company/lunionlab\">LinkedIn</a>\n" + //
                "            </div>\n" + //
                "            <p>&copy;" + formatDate(now, "yyyy") + "Lunion-Lab. Tous droits réservés.</p>\n" + //
                "        </div>\n" + //
                "    </div>\n" + //
                "</body>\n" + //
                "</html>";
        return output;
    }

    public static boolean verifyEmail(String email) {
        String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern emailPattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = emailPattern.matcher(email);
        return matcher.matches();
    }

    public static ProfileModel getUserAuth() {
        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        String email = authUser.getName();
        System.out.println(email);
        return profileService.getProfileByEmail(email);
    }

    public static String getUserAuthEmail() {
        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        String email = authUser.getName();
        return email;
    }

    public static String convertImageToBase64() {
        try {
            File file = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "logo_fn.png");
            InputStream in = new FileInputStream(file);
            byte[] filcontent;
            filcontent = in.readAllBytes();
            in.close();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(filcontent);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
