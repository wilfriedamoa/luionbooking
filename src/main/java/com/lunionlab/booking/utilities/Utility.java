package com.lunionlab.booking.utilities;

import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;

import com.lunionlab.booking.models.ProfileModel;
import com.lunionlab.booking.services.ProfileService;
import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;

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

    public static boolean sendMailWithResend(String from, String to, String subject, String code) {
        Resend resend = new Resend("re_KJJw4scL_EcdyogNFyz2C6oz9stqvUfjs");
        CreateEmailOptions params = CreateEmailOptions.builder().from(from).to(to).subject(subject)
                .html(emailTemplate(code)).build();
        try {
            CreateEmailResponse data = resend.emails().send(params);
            System.out.println(data);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static String emailTemplate(String code) {
        String ouput = "<!DOCTYPE html>\n" + //
                "<html lang=\"fr\">\n" + //
                "<head>\n" + //
                "    <meta charset=\"UTF-8\">\n" + //
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" + //
                "    <title>Lunion-Booking Email Verification</title>\n" + //
                "    <style>\n" + //
                "        body {\n" + //
                "            font-family: Arial, sans-serif;\n" + //
                "            background-color: #f0f0f0;\n" + //
                "            display: flex;\n" + //
                "            justify-content: center;\n" + //
                "            align-items: center;\n" + //
                "            height: 100vh;\n" + //
                "            margin: 0;\n" + //
                "        }\n" + //
                "        .card {\n" + //
                "            background-color: white;\n" + //
                "            padding: 40px;\n" + //
                "            border-radius: 8px;\n" + //
                "            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);\n" + //
                "            text-align: center;\n" + //
                "            max-width: 400px;\n" + //
                "            margin:0px auto;\n" + //
                "        }\n" + //
                "        .logo {\n" + //
                "            width: 100px;\n" + //
                "            margin-bottom: 20px;\n" + //
                "        }\n" + //
                "        h1 {\n" + //
                "            color: hsl(348, 77%, 58%);\n" + //
                "            margin-bottom: 20px;\n" + //
                "        }\n" + //
                "        .code {\n" + //
                "            font-size: 36px;\n" + //
                "            font-weight: bold;\n" + //
                "            margin: 20px 0;\n" + //
                "            color: hsl(348, 77%, 58%);\n" + //
                "        }\n" + //
                "        .footer {\n" + //
                "            margin-top: 40px;\n" + //
                "            font-size: 12px;\n" + //
                "            color: #666;\n" + //
                "        }\n" + //
                "        .social-icons {\n" + //
                "            margin-top: 20px;\n" + //
                "        }\n" + //
                "        .social-icons a {\n" + //
                "            margin: 0 5px;\n" + //
                "            color: hsl(348, 77%, 58%);\n" + //
                "            text-decoration: none;\n" + //
                "        }\n" + //
                "    </style>\n" + //
                "</head>\n" + //
                "<body>\n" + //
                "    <div class=\"card\">\n" + //
                "        <img src=\"https://media.licdn.com/dms/image/D4D22AQGSvGeYz_Egbw/feedshare-shrink_800/0/1714161865653?e=2147483647&v=beta&t=1Ury-AvA7IPZQl6h_URuROAbvT1meu34t07llzAOGMM\" alt=\"Lunion-Booking Logo\" class=\"logo\">\n"
                + //
                "        <h1>Verify your email</h1>\n" + //
                "        <p>Hello,</p>\n" + //
                "        <p>Your Lunion-Booking verification code is</p>\n" + //
                "        <div class=\"code\">" + code + "</div>\n" + //
                "        <p>If you did not request this code, please ignore this email. If you have any issues, please do not hesitate to contact support at <a href=\"mailto:lunionlab@gmail.com\">lunionlab@gmail.com</a>.</p>\n"
                + //
                "        <p>Thanks,<br>Lunion-Lab Team</p>\n" + //
                "        <div class=\"footer\">\n" + //
                "            <img src=\"https://media.licdn.com/dms/image/D4D22AQGSvGeYz_Egbw/feedshare-shrink_800/0/1714161865653?e=2147483647&v=beta&t=1Ury-AvA7IPZQl6h_URuROAbvT1meu34t07llzAOGMM\" alt=\"Lunion-Lab Logo\" class=\"logo\">\n"
                + //
                "            <div class=\"social-icons\">\n" + //
                "                <a href=\"#\">Twitter</a>\n" + //
                "                <a href=\"#\">YouTube</a>\n" + //
                "                <a href=\"#\">LinkedIn</a>\n" + //
                "            </div>\n" + //
                "            <p>Made for you by the Lunion-Lab team</p>\n" + //
                "        </div>\n" + //
                "    </div>\n" + //
                "</body>\n" + //
                "</html>";

        return ouput;
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
}
