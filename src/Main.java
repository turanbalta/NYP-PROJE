import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;


// Uye Classı
class Uye {
    private String ad;
    private String soyad;
    private String email;

    public Uye(String ad, String soyad, String email) {
        this.ad = ad;
        this.soyad = soyad;
        this.email = email;
    }

    public String getAd() {
        return ad;
    }

    public String getSoyad() {
        return soyad;
    }

    public String getEmail() {
        return email;
    }

    public String toString() {
        return ad + "\t" + soyad + "\t" + email;
    }

}

// EliteUye ve GenelUye classları kalıtım ile Uye Classına bağlı
class EliteUye extends Uye {
    public EliteUye(String ad, String soyad, String email) {
        super(ad, soyad, email);
    }
}

class GenelUye extends Uye {
    public GenelUye(String ad, String soyad, String email) {
        super(ad, soyad, email);
    }
}



public class Main {

    // ÜYE KAYDI
    public static void kayit(Uye uye) {

        String grup = "";
        if (uye instanceof EliteUye){
            grup = "ELİT ÜYELER";
        }
        else {
            grup = "GENEL ÜYELER";
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader("kullanıcılar.txt"));
            ArrayList<String> liste = new ArrayList<>();
            String satir;


            while ((satir = reader.readLine()) != null) {
                liste.add(satir);
            }

            reader.close();

            boolean baslik = false;
            int baslikIndeksi = -1;
            for (int i = 0; i < liste.size(); i++) {
                String str = liste.get(i);
                if (str.trim().equals(grup)) {
                    baslik = true;
                    baslikIndeksi = i;
                    break;
                }
            }

            if (!baslik) {
                baslikIndeksi = liste.size();
                liste.add(grup);
            }

            liste.add(baslikIndeksi + 1, uye.toString());


            BufferedWriter writer = new BufferedWriter(new FileWriter("kullanıcılar.txt"));
            for (String no : liste) {
                writer.write(no + "\n");
            }

            writer.close();
            System.out.println("Üye kaydedildi.");
        }
        catch (IOException e) {
            System.out.println("Dosya yazma hatası: " + e.getMessage());
        }

    }

    // DOSYADAN ELİT ÜYELERİN MAİLLERİNİ ALMA
    public static ArrayList<String> getElitMails(String fileName) {
        ArrayList<String> elitMails = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(fileName))) {
            String line;
            boolean inElitSection = false;
            boolean inGenelSection = false;
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                if (line.equals("ELİT ÜYELER")) {
                    inElitSection = true;
                    inGenelSection = false;
                } else if (line.equals("GENEL ÜYELER")) {
                    inElitSection = false;
                    inGenelSection = true;
                } else if (inElitSection) {
                    String[] parts = line.split("\t");
                    elitMails.add(parts[2]);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Dosya bulunamadı: " + e.getMessage());
        }
        return elitMails;
    }

    //DOSYADAN GENEL ÜYELERİN MAİLLERİNİ ALMA
    public static ArrayList<String> getGenelMails(String fileName) {
        ArrayList<String> genelMails = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(fileName))) {
            String line;
            boolean inElitSection = false;
            boolean inGenelSection = false;
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                if (line.equals("ELİT ÜYELER")) {
                    inElitSection = true;
                    inGenelSection = false;
                } else if (line.equals("GENEL ÜYELER")) {
                    inElitSection = false;
                    inGenelSection = true;
                } else if (inGenelSection) {
                    String[] parts = line.split("\t");
                    genelMails.add(parts[2]);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Dosya bulunamadı: " + e.getMessage());
        }
        return genelMails;
    }

    //DOSYADAN TÜM YÜELERİN MAİLLERİNİ ALMA
    public static ArrayList<String> getAllMails(String fileName) {
        ArrayList<String> allMails = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(fileName))) {
            String line;
            boolean inElitSection = false;
            boolean inGenelSection = false;
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                if (line.equals("ELİT ÜYELER")) {
                    inElitSection = true;
                    inGenelSection = false;
                } else if (line.equals("GENEL ÜYELER")) {
                    inElitSection = false;
                    inGenelSection = true;
                } else if (inElitSection || inGenelSection) {
                    String[] parts = line.split("\t");
                    allMails.add(parts[2]);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Dosya bulunamadı: " + e.getMessage());
        }
        return allMails;
    }



    // MAİL GÖNDERİMİ İÇİN PARAMETRELERİ GÖNDERİYORUZ
    public static void sendMailToElit(ArrayList<String> elitMailler, String konu, String mesaj) {
        sendMail(elitMailler, konu, mesaj);
    }

    // MAİL GÖNDERİMİ İÇİN PARAMETRELERİ GÖNDERİYORUZ
    public static void sendMailToGenel(ArrayList<String> genelMailler, String konu, String mesaj) {
        sendMail(genelMailler, konu, mesaj);
    }

    // MAİL GÖNDERİMİ İÇİN PARAMETRELERİ GÖNDERİYORUZ
    public static void sendMailToAll(ArrayList<String> elitMailler, ArrayList<String> genelMailler, String konu, String mesaj) {
        ArrayList<String> toList = new ArrayList<>(elitMailler);
        toList.addAll(genelMailler);
        sendMail(toList, konu, mesaj);
    }


    // MAİL GÖNDERME İŞLEMİ
    private static void sendMail(ArrayList<String> toList, String konu, String mesaj) {
        final String username = "1turanbalta@gmail.com"; // GMAIL hesabı kullanılacaksa, mail adresi girilmeli
        final String password = "wwbhrqcnlnihvuye"; // GMAIL hesabı kullanılacaksa, şifre girilmeli

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            InternetAddress[] toAddresses = new InternetAddress[toList.size()];
            for (int i = 0; i < toList.size(); i++) {
                toAddresses[i] = new InternetAddress(toList.get(i));
            }
            message.setRecipients(Message.RecipientType.TO, toAddresses);
            message.setSubject(konu);
            message.setText(mesaj);

            Transport.send(message);
            System.out.println("Mail gönderimi başarılı.");
        } catch (MessagingException e) {
            System.out.println("Mail gönderimi başarısız. Hata: " + e.getMessage());
        }
    }




    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        File file = new File("kullanıcılar.txt");
        ArrayList<EliteUye> eliteUyeler = new ArrayList<>();
        ArrayList<GenelUye> genelUyeler = new ArrayList<>();
        ArrayList<Uye> uyes = new ArrayList<>();
        uyes.addAll(eliteUyeler);
        uyes.addAll(genelUyeler);


        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        while (true) {
            System.out.println("------------------");
            System.out.println("1- Elite Üye Ekle\n2- Genel Üye Ekle\n3- Mail Gönder");
            System.out.println("------------------");

            String secim = scanner.nextLine();

            // ELİT ÜYE EKLEMEK İÇİ SEÇİLİR
            if (secim.equals("1")) {
                System.out.print("Adınızı girin: ");
                String ad = scanner.nextLine();
                System.out.print("Soyadınızı girin: ");
                String soyad = scanner.nextLine();
                System.out.print("E-posta adresinizi girin: ");
                String email = scanner.nextLine();


                EliteUye eliteUye = new EliteUye(ad, soyad, email);
                eliteUyeler.add(eliteUye);
                Main.kayit(eliteUye);

            }

            // GENEL ÜYE EKLEMEK İÇİN SEÇİLİR
            else if (secim.equals("2")) {
                System.out.print("Adınızı girin: ");
                String ad = scanner.nextLine();
                System.out.print("Soyadınızı girin: ");
                String soyad = scanner.nextLine();
                System.out.print("E-posta adresinizi girin: ");
                String email = scanner.nextLine();

                GenelUye genelUye = new GenelUye(ad, soyad, email);
                genelUyeler.add(genelUye);
                Main.kayit(genelUye);

            }

            // MAİL GÖNDERME İŞLEMLERİ İÇİN SEÇİLİR
            else if (secim.equals("3")) {
                System.out.println("---------------------");
                System.out.println("1- Elit Üyelere Mail\n2- Genel Üyelere Mail\n3- Tüm Üyelere Mail");
                System.out.println("---------------------");

                String mailTercih = scanner.nextLine();


                ArrayList<String> elitMails = getElitMails("kullanıcılar.txt");
                ArrayList<String> genelMails = getGenelMails("kullanıcılar.txt");
                ArrayList<String> allMails = getAllMails("kullanıcılar.txt");

                // ELİT ÜYELERE MAİL GÖNDERİMİ
                if (mailTercih.equals("1")) {

                    System.out.println("Mailin Konusu : ");
                    String topic = scanner.nextLine();
                    System.out.println("Mesajınız : ");
                    String message = scanner.nextLine();

                    sendMailToElit(elitMails,topic,message);


                }

                // GENEL ÜYELERE MAİL GÖNDERİMİ
                else if(mailTercih.equals("2")) {

                    System.out.println("Mailin Konusu : ");
                    String topic = scanner.nextLine();
                    System.out.println("Mesajınız : ");
                    String message = scanner.nextLine();

                    sendMailToGenel(genelMails,topic,message);


                }

                // TÜM ÜYELERE MAİL GÖNDERİMİ
                else if(mailTercih.equals("3")) {

                    System.out.println("Mailin Konusu : ");
                    String topic = scanner.nextLine();
                    System.out.println("Mesajınız : ");
                    String message = scanner.nextLine();

                    sendMailToAll(elitMails,genelMails,topic,message);


                }

                else {
                    break;
                }

            }
            else {
                break;
            }
        }






    }
}