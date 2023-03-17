/* Kodeskjelett for 
 * obligatorisk oppgave V-2022: Medlemsregister
 */ 
import static java.lang.System.*;
import static javax.swing.JOptionPane.*; 
import static java.lang.Integer.*;
import java.util.*; 
import java.io.*;
import java.sql.*;
public class PhilipMedlemmer  { 

  private static File dbFil = new File("medlemmer.db");
  private static String sql ="";
  private static String url = "jdbc:sqlite:Medlemmer.db";
  
  public static void main(String[] args) throws Exception { 
   if (!dbFil.exists())   
    lagNyTabell();               
  int valg = 0;
  do {
    valg = visMeny(); 
    if (valg != 0 )  
      switch ( valg ) { 
      case 1: visAlleEtternavn(); break;
      case 2: visAlleTlf();       break;
      case 3: registrereMedlem(); break;
      case 4: endreMedlem();      break;
      case 5: slettMedlem();      break;
      case 6: taBackup();         break;
      case 7: hentBackup();       break;
      default: break;
      }
    } while ( valg != 0);  
  }  
  // Hjelpemetoder kun til bruk i dette programmet
  public static int visMeny() {   
    String meny = "[1] Vis alle etternavn" + "\n" 
    + "[2] Vis alle tlf.nr" + "\n"  
    + "[3] Legg til medlem" + "\n"   
    + "[4] Endre medlem" + "\n"   
    + "[5] Slett medlem" + "\n" 
    + "[6] Ta backup" + "\n"   
    + "[7] Hent backup" + "\n"
    + "[0] Avslutt";    
    return parseInt(showInputDialog(meny + "\n" + "Velg et nr. (0-7):"));
  }
  // Her oppretter vi en ny tabell. 
  //Denne kan kjøres en gang og kommenteres ut, eller så er det mulig å slette Medlemmer.db via filer
  //Annen alternativ er å bruke drop table noe lignende. 
  //Måten jeg oppretter tabellen, har jeg hentet fra StudentDB.java fra forlesningen
  //Denne vil iallefall dukke opp hvis Tabellen ikke eksisterer.
  // Det vil komme en showMessageDialog når man lager denne tabellen.
  private static void lagNyTabell() throws Exception {
    try{
      showMessageDialog(null, "Start: Lager db-tabellen Medlem"); 
      sql = "create table Medlemmer (Nr integer primary key, fNavn varchar(20), eNavn varchar(20), adresse varchar(50), tlfNr integer(10))"; // Kan bruke auto_increment også
      Connection conn = DriverManager.getConnection(url);
      Statement stmt = conn.createStatement();
      stmt.executeUpdate(sql);

       // Legger inn medlemdataen 

      File register = new File ("register.txt");
      Scanner leser = new Scanner (register);

      while (leser.hasNextLine()){
        String linje = leser.nextLine();

        String[] dTab = linje.split(";");
        int nr = parseInt (dTab[0]);
        String fNavn = dTab[1];
        String eNavn = dTab[2];
        String adresse = dTab[3];
        int tabLengde = dTab.length; // Vi bruker dTab.length slik at vi vil strekke oss til den siste plassen av indeksen.
        int tlf = 0;                 // Måten jeg har satt dette opp er inspirert av tidligere gjennomgang i lab timene og forlesninger fra Kap 5 og 7. 
        
        boolean registrert;           // Denne måten har jeg hentet inspirasjon fra eksempler fra Kap 3. fra: https://dbsys.info/programmering/kap3kontroll/eks.html
                                      // 03.Myndig.
                                      // Istedet for myndig, så tar jeg å sjekker i forhold til om medlemnr eksisterer ved å sette en if og else itillegg. 
        if (tabLengde == 5) {
          registrert = true;
        }
        else {
          registrert = false;
        }
        if (registrert == true) {       //Registrerte nr. Det vil ikke skje noe annet enn at det blir godkjent ved bruk av scanner.
                                        // Grunnen til at det står == 5 er fordi index 4 går fra 0, 1, 2 ,3 , 4. Vi skriver heller ikke tallet i en blokk parantes []. 
          tlf = parseInt(dTab[4]);      //Men skriver man [4] forteller vi editoren at det er indeks 4, altså den 5. indeksen. I register.txt så er telefonnr plassert på indeks 4 ved bruk av split (;).
        }
        if (registrert == false){       // Ikke registrerte medlemmer ved hjelp av Boolean og if-setninger som vi har lært.
          tlf = 0;                                // Lignende oppgaver i forhold til if-setninger og boolean har blitt brukt og knyttet sammen med denne oppgaven bl.a
        }
        out.println("Medlemnr: "+nr+" - " + fNavn + " - " + eNavn + " - " + adresse + " " + tlf);
        
        sql = "insert into Medlemmer values(" + nr + ",'" + fNavn + "','"+ eNavn + "','" + adresse + "'," + tlf +");";
        stmt.executeUpdate(sql);
      }
    }
    catch (SQLException e) {
      showMessageDialog(null,"Oppkobling til databasen feilet! \n Se detaljer i konsollet");
      out.println("Oppkobling til databasen " + url + " feilet.\n" + e.toString());
    }
    catch (ArrayIndexOutOfBoundsException  e) {
      out.println("Det er ikke registrert noen telefonnr hos enkelte medlemmer" + "\n" +
        "Andre muligheter: Flere verdier enn antall kolonner");             
                                                                              // F.eks Hvis jeg hadde hatt int tlf = parseInt(dTab[4]); istedet for tabLengde og if setninger.
                                                                              // Tekstfilen (register.txt) viser ikke verdier for de som ikke har registert tlfnr
                                                                              // I dette tilfelle har vi gitt de som ikke har registert 0 for å gjøre det lettvint utover progammeringen
    }
  } 
  //Her bruker jeg sql, connection, statement, resultset med whileloop for å lese igjennom tabellen fra databasen
  //Dette er noe lignende med i lagNyTabell. Men jeg bruker whileloop i tilegg på å lese og finne etternavn.
  //Jeg bruker getString med outprint for at den skal vise alle etternavnene i konsollet. 
  //Sorteringen skjer i spørringen (sql), mens resultset = stmt.excecuteQuery(sql) gjør handlingen.
  // Spørringen er hentet fra https://www.w3schools.com/sql/sql_orderby.asp
  // dette er også noe vi har lært i database 1

  public static void visAlleEtternavn() throws Exception {   
    try {
      showMessageDialog(null, "1: Alle medlemmer, sortert på etternavn");
      sql = "select * from Medlemmer order by eNavn;";
      Connection conn = DriverManager.getConnection(url);
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()){
        int nr = rs.getInt("Nr");
        String eNavn = rs.getString("eNavn");
        if (nr < 10)
          out.println("Medlemnr: " + "0" + nr + "|" + "Etternavn: " + eNavn);
        else {
          out.println("Medlemnr: " + nr + "|" + "Etternavn: " + eNavn);
        }     
      }
    }
    catch (SQLException e) {
      showMessageDialog(null,"Oppkobling til databasen feilet! \n Se detaljer i konsollet");
      out.println("Oppkobling til databasen " + url + " feilet.\n" + e.toString());
    }
  }
//Her skal medlemnr som ikke har telefonnr ikke vises.
//Fra mitt perspektiv er det mer logisk å se hvem medlemnr som ikke har registrert tlfnr, slik at man kan endre hvis man ønsker det
// Med If og else setninger har jeg gjort at de som ikke har registrert tlfnr, altså gjort om til 0 fra tidligere koding i lagNyTabell
// 0 = "ikke registrert tlfnr".
// Hvis jeg ville ha "fjernet" de som ikke har telfeonnr hadde jeg tatt en if-setning som forteller at det kan kun vises med de som har 8 siffer

  public static void visAlleTlf() { 
    try {
      sql = "select * from Medlemmer order by tlfNr ASC;"; // Kan også bruke DESC istedet for ASC
      Connection conn = DriverManager.getConnection(url);
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()){
        int nr = rs.getInt("Nr");
        int tlf = rs.getInt("tlfNr");
        if (tlf == 0) {
          out.println("Medlem: " + nr + "|" + "Ikke registrert telefonnr");
        }
        else {
          out.println("Medlem: " + nr + "|" + "Telefon:" + tlf);
        }

      }
      showMessageDialog(null, "2: Alle medlemmer med tlf, sortert på tlf.nr");
    }
    catch (SQLException e) {
      showMessageDialog(null,"Oppkobling til databasen feilet! \n Se detaljer i konsollet");
      out.println("Oppkobling til databasen " + url + " feilet.\n" + e.toString());
    }
  }  
  // Husk å skrive logg-meldinger i konsollet for endringsmetoder!
  //Annen mulighet er å bruke auto increment som teller automatisk medlemnr, men dette har ikke jeg.
  // Her har jeg brukt showInputDialog for å kunne fylle på selv. 
  // Ulemper med denne koden er når man skriver inn medlemnr, så kommer det en feilmelding ved eksisterende medlemnr. (Den sier ikke i fra).
  // Mye av disse metodene har jeg lært fra tidligere kaptiler 2 og 3 fra 1.semester 
  // Hvis det er flere ting jeg ville ha gjort her så er det blant annet å ikke skrive tall og minimum bokstaver i fornavn og etternavn. 
  // Dette hadde jeg gjort med if-setninger eller bruke catch passende exception i forhold til feilmeldinger.
  private static void registrereMedlem() throws Exception { 
    try {     
      Connection conn = DriverManager.getConnection(url);
      Statement stmt = conn.createStatement();
      stmt.executeUpdate(sql);
      int nr = parseInt(showInputDialog("Gi medlemnr"));
      String fNavn = showInputDialog("Gi fornavn");
      String eNavn = showInputDialog("Gi etternavn");
      String adresse = showInputDialog("Gi adresse");
      int tlfNr = parseInt(showInputDialog("Gi tall"));
      if (tlfNr < 10000000 || tlfNr > 99999999){
        showMessageDialog(null,"Det må være åtte sifferede! Prøv igjen"); 
      }
      else if (tlfNr >= 10000000 || tlfNr <= 99999999) {
       sql = "insert into Medlemmer values (" + nr + ",'" + fNavn + "','"+ eNavn + "','" + adresse + "'," + tlfNr +");";
       showMessageDialog(null, "3: Et nytt medlem er registrert i systemet!" + "\n" +
        "Medlemnr : " + nr + "\n" +
        "Fornavn  : " + fNavn + "\n" +
        "Etternavn: " + eNavn + "\n" +
        "Adresse  : " + adresse + "\n" +
                                "Telefon  : " + tlfNr);             // Ikke nødvendig, men greit å se over hva man har skrevet i registeret. 
       stmt.executeUpdate(sql);                                     // Siden det er en del ting man fyller på så er det greit å dobbelt sjekke over hva man har skrevet.
     }
   }

   catch (SQLException e) {
    showMessageDialog(null,"Oppkobling til databasen feilet! \n Se detaljer i konsollet");
    out.println("Oppkobling til databasen " + url + " feilet.\n" + e.toString());
  }
  catch (NumberFormatException e) {
    out.println("NumberFormatException: " + "Du er nødt til å legge inn Medlemnr med tall og ikke bokstaver");
  }
}

  // Jeg bruker select spørring for å sikte mot tabellen Medlemmer.
  // Her har man muligheten til å redigere/legge til telefonnr
  // Her skriver man hvilke medlemnr man ønsker å legge til eller endre tlfnr
  // Systemet vil sjekke om Medlemnr eksisterer ved hjelp av if-setninger og boolean
  // Vi skriver hvem medlemnr i showInputDialog
  // Det blir sjekket om medlemnr eksiterer eller ikke ved hjelp av boolean og SQL-spørring med Where
  // Hvis det man skriver i showInputDialog stemmer med et medlemnr vil man kunne gi et nytt tlfnr
  //Derfor bruker vi Update og Where i SQL. slik at vi ser på medlemnr og oppdaterer tlfnr 

private static void endreMedlem() throws Exception { 
  try {
    showMessageDialog(null, "4: Endre eller legge til tlf.nr");  
    Connection conn = DriverManager.getConnection(url);
    Statement stmt = conn.createStatement();
    sql = "select * from Medlemmer;";
    ResultSet rs = stmt.executeQuery(sql);
    int mNr = parseInt(showInputDialog("Hvilke medlem vil du legge til eller endre telefonnr"));
    boolean eksiterer = false;
    while (rs.next()){
      int nr = rs.getInt("Nr");
      int tlf = rs.getInt("tlfNr");

      if (mNr == nr) {
        eksiterer = true;
        tlf = parseInt(showInputDialog("Medlemnr: " + mNr + " Gi nytt telefonnr"));
        if (tlf < 10000000 || tlf > 99999999) {
          showMessageDialog(null, "Kun 8 siffer");
        }  
        
        sql = "update Medlemmer set tlfNr = "+tlf+" where Nr = "+mNr+";";
        stmt.executeUpdate(sql);
        showMessageDialog(null, "Nytt telefonnr for Medlemnr: " + nr + " er registrert som: " + tlf);
        break;
      }
    }

    if (eksiterer == false || !eksiterer ) {
      showMessageDialog(null, "Medlemnr eksiterer ikke!" );
    }

  }
  catch (SQLException e) {
    showMessageDialog(null,"Oppkobling til databasen feilet! \n Se detaljer i konsollet");
    out.println("Oppkobling til databasen " + url + " feilet.\n" + e.toString());
  }
  catch (NumberFormatException e) {
    out.println("NumberFormatException: " + "Du er nødt til å skrive inn Medlemnr(tall) og ikke bokstaver");
  }
}
// I denne koden fjerner vi en medlem ved hjelp av Medlemnr ved bruk av SQL spøring og if setning
// Vi vil vite om Medlemnr eksisterer. Ved å gjøre dette kan vi bruke boolean i if setningene
// Vi sikter mot hele tabellen ved å ta Select * from Medlemmer. Dette må vi gjøre for å kunne vite og slette.
//Vi har med Rs slik at vi har noe form som kan oppdatere (fjerne) hvert enkelt kolonne.
//Skriver jeg et tall i showInputDialog som stemmer med et av Medlemnr, vil det bli slettet
// Dette blir gjort ved en SQL spørring, altså Where. Hvis tallet jeg skriver er lik medlemnr vil det skje en oppdatering
//Hvis man skriver et medlemnr som ikke ekstirerer i showInputDialog, så vil den gå i den andre if setningen som sier at den ikke finner medlemnr 
// I forhold til showInputDialogen. 

private static void slettMedlem() throws Exception {   
  try {
    showMessageDialog(null, "5: Slette medlem");
    Connection conn = DriverManager.getConnection(url);
    Statement stmt = conn.createStatement();
    sql = "select * from Medlemmer;";
    ResultSet rs = stmt.executeQuery(sql);
    int mNr = parseInt(showInputDialog("Hvilke medlemnr vil du fjerne fra systemet?"));
    boolean eksiterer = false;

    while (rs.next()){
      int nr  = rs.getInt("Nr");
      String fNavn = rs.getString("fNavn");
      String eNavn = rs.getString("eNavn");
      String adresse = rs.getString("Adresse");
      int tlf = rs.getInt("tlfNr");      
      
      if (mNr == nr) {
        eksiterer = true;
        showMessageDialog(null, "Medlemnr: " + mNr + " er fjernet fra systemet.");
        sql = "delete from Medlemmer where Nr = "+mNr+";";
        stmt.executeUpdate(sql);
        break;
      }
    }
    
    if (!eksiterer) {
      showMessageDialog(null, "Medlemnr: " + mNr + " eksiterer ikke!" + "\n" +
        "Legg til en ny medlem!");
    }
  }
  catch (SQLException e) {
    showMessageDialog(null,"Oppkobling til databasen feilet! \n Se detaljer i konsollet");
    out.println("Oppkobling til databasen " + url + " feilet.\n" + e.toString());
  }
  catch (NumberFormatException e) {
    out.println("NumberFormatException: " + "Du er nødt til å skrive inn Medlemnr(tall) og ikke bokstaver");
  }
}

// Vi har connection og Statement i forhold til database. 
//  Vi ønsker å lagre nåværende data som backup i en tekstfil
//  Vi bruker PrintWriter for å lage tekstfilen bupMedlemmer.txt
//  Gjennom sql spørring vil vi hente hele tabellen ved select from Medlemmer (hele tabellen)
// Med resultset ønsker vi hvert enkelt kolonne (medlemnr, fornavn,etternavn, adresse og tlf)
// Vi bruker skriver.println for å skrive det i selve tekstfilen, bupMedlemmer.txt
// Vi må lukke PrintWriteren ved å ta skriver.close, hvis ikke vil ikke det være fungerende å ta backup
// Vi vil ha med outprintln for å bekrefte at Backupen er ok.
// Denne delen er veldig har noe likhet i forhold til oppgaver vi har jobbet med i Kap 7 fra første semester. 
// Blant annet eksamensoppgaver.
private static void taBackup() throws Exception { 
  try { 
   Connection conn = DriverManager.getConnection(url);
   Statement stmt = conn.createStatement();
   PrintWriter skriver = new PrintWriter("bupMedlemmer.txt"); 
   sql = "select * from Medlemmer;";
   ResultSet rs = stmt.executeQuery(sql);
   while (rs.next()){
    int nr = rs.getInt("Nr");
    String fNavn = rs.getString("fNavn");
    String eNavn = rs.getString("eNavn");
    String adresse = rs.getString("Adresse");
    int tlf = rs.getInt("tlfNr");      
    skriver.println(nr+";"+fNavn+";"+eNavn+";"+adresse+";"+tlf);
  }
  skriver.close();
  out.println("Lager en Backup tekstfil... OK!");
}  
catch (SQLException e) {
  showMessageDialog(null,"Oppkobling til databasen feilet! \n Se detaljer i konsollet");
  out.println("Oppkobling til databasen " + url + " feilet.\n" + e.toString());
}

}

   //En del Lik lagNyTabell. Bare at man "Henter" fra bupMedlemmer.txt og ikke register.txt
  //Her må man se om tabellen eksiterer fra før eller ikke ved å se feilmeldingen
  //Kommenter ut i forhold til feilmeldingen. Hvis det står at tabellen allerede eksisterer
  //-- HVIS TABELLEN ALLEREDE EKSISTERER SÅ:
  //1. Kommenter ut create Table -> La Droptable være aktiv    -> Kompiler -> Kjør -> "feilmelding oppstår", men du har bare droppet tabellen
  //2. Kommenter ut Drop Table   -> La create Table være aktiv -> Kompiler -> Kjør -> Listen vil vises
  // Denne koden kunne trengt oppgraderinger, men den fungerer. På en tungvint måte vet jeg.
  // Det er nok mulig å gjøre den bedre slik at vi slipper å kommentere ut, men dette synes jeg var utfordrende. 
  // Alle telefonnr som ikke registrert er blitt gjort om til 0 som gjør at jeg ikke trenger if setninger i forhold til det
  // Hadde jeg holdt koden slik med register.txt hadde det blitt OutofBounds feilmelding. 

private static void hentBackup() throws Exception { 
  try{ 

    showMessageDialog(null, "7: Hent backup");
    String url = "jdbc:sqlite:medlemmer.db";
    Connection conn = null;
    conn = DriverManager.getConnection(url);
    //sql = "Drop table Medlemmer";
    sql = "create table Medlemmer (Nr integer primary key, fNavn varchar(20), eNavn varchar(20), Adresse varchar(50), tlfNr integer(10))";
    Statement stmt = conn.createStatement();
    stmt.executeUpdate(sql);

      // Legger inn medlemdataen 
    File register = new File ("bupMedlemmer.txt");
    Scanner leser = new Scanner (register);
    while (leser.hasNextLine()){
      String linje = leser.nextLine();
      String[] dTab = linje.split(";");
      int nr = parseInt (dTab[0]);
      String fNavn = dTab[1];
      String eNavn = dTab[2];
      String adresse = dTab[3];
      int tlf = parseInt(dTab[4]); // I motsetning til lagNyTabell så kan jeg skrive (dTab[4]) fordi jeg la til 0 som verdi for de som ikke hadde tlfnr.
      
      sql = "insert into Medlemmer values(" + nr + ",'" + fNavn + "','"+ eNavn + "','" + adresse + "'," + tlf +");";
      stmt.executeUpdate(sql);
    }
      // Leser av tabellen, men sortert på fornavn
    sql = "select * from Medlemmer order by fNavn;";
    ResultSet rs = stmt.executeQuery(sql);
    while (rs.next()){
      int nr = rs.getInt("Nr");
      String fNavn = rs.getString("fNavn");
      String eNavn = rs.getString("eNavn");
      String adresse = rs.getString("Adresse");
      int tlf = rs.getInt("tlfNr");
      
      if (tlf == 0) {
        out.println("Medlem: "+nr+" - " + fNavn + " - " + eNavn + " - " + adresse + " - " + "Ikke registrert telefonnr");
      }
      else {
        out.println("Medlem: "+nr+" - " + fNavn + " - " + eNavn + " - " + adresse + " - "  + tlf);
      }
    }
  }     
  catch (SQLException e) {
    showMessageDialog(null,"Oppkobling til databasen feilet! \n Se detaljer i konsollet");
    out.println("Oppkobling til databasen " + url + " feilet.\n");
    out.println("Tabellen eksisterer allerede. Drop tabellen og lag en ny tabell.");
    out.println("Programmet må kjøres 2 ganger!");
    out.println("1. Kommenter ut create table. Nå kan du droppe tabellen." + "Du vil fortsatt få denne feilmeldingen ved droptable, men du gjør ikke noe feil!");
    out.println("2. Kommenter ut drop table. Nå kan du create tabellen.");
    out.println("Det fungerer kun ved 1 kjøring. Skal du se resultatet, så må du repetere");

  }
  catch (FileNotFoundException e) {
    out.println("Feilmelding i forhold til filer. Se nøye etter   feil i forhold til koder som er involvert med filer");     
  }
}
}

// Siste kommentar:
// I løpet av oppgaven har det vært lærrerikt å løse de 7 forskjellige switchenhe.
// For min del synes jeg det var en fin måte å "repetere" ting vi har gjort tidligere 
// Ved å implementere i denne typen oppgaven, spørs hvordan du ønsker å løse oppgaven
// Jeg har hentet mye inspirasjon fra oppgavene vi har gjort tidligere i lab og forlesninger
// Mye faglig stoff som jeg har knyttet i denne oppgaven fra Kap 2, 3 og 7. 
// Her er det også mulig å bruke metoder fra Kap 4, men det gjorde ikke jeg. 
// Selv om det har vært lærerrikt så har det også vært utfordrende, men jeg merker at det 
// å være tilstede fysisk i undervisningen har hjulpet veldig i forhold til denne oppgaven. 
// Jeg har samarbeided med andre studenter i forhold til denne obligatoriske oppgaven.
// Det har vært hjelpsomt og lærerrikt dette også med å hjelpe hverandre. 
// Mye av "Database" delen er blitt kopiert og limt inn.
// I tidligere forlesninger gikk vi gjennom i forhold til Databaser og lagde StudentDB
// Jeg har brukt mye av dette som inspirasjon. Dette har hjulpet meg mye med å løse denne oppgaven i forhold til SQL delen.
// Jeg har ikke løst oppgaven på en kronologisk rekkefølge, så det har vært frem og tilbake.