import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Created by Jeff on 2/17/2015.
 */

public class DZ_Eboxes {

    public static class Ebox {

        public Ebox() {
            latitude = 0;
            longitude = 0;

        }

        public Ebox(double lat, double lon) {
            latitude = lat;
            longitude = lon;
        }

        private double latitude;
        private double longitude;

        public void setLatitude(double lat) {
            latitude = lat;
        }

        public void setLongitude(double lon) {
            longitude = lon;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

    }

    public void printToCSV(Ebox[] array) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter outFile = new PrintWriter("outFile.csv", "UTF-8");
        for (int i = 0; i < array.length; i++) {
            outFile.println(array[i].getLongitude() + "," + array[i].getLatitude());
        }
        outFile.close();
    }



    public static Ebox[] parseXML(File XML) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {

//            FileInputStream file = new FileInputStream(new File("src/DrexelBoxes.xml"));
            FileInputStream file = new FileInputStream(XML);

            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder builder =  builderFactory.newDocumentBuilder();

            Document xmlDocument = builder.parse(file);

            XPath xPath =  XPathFactory.newInstance().newXPath();

            String expression = "/Folder/Placemark/Point/coordinates";
//            System.out.println(expression);
            NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);

            Ebox[] eboxList = new Ebox[nodeList.getLength()+1];
            for( int i = 0; i < eboxList.length; i++){
                eboxList[i] = new Ebox();
            }
            System.out.println( nodeList.getLength()+1 );
            String tmp;
            double tmpLon;
            double tmpLat;

            for (int i = 0; i < nodeList.getLength(); i++) {
                tmpLon = Double.valueOf(nodeList.item(i)
                        .getFirstChild()
                        .getNodeValue()
                        .toString()
                        .split(",")[0]);
                tmpLat = Double.valueOf(nodeList.item(i)
                        .getFirstChild()
                        .getNodeValue()
                        .toString()
                        .split(",")[1]);
                eboxList[i].setLongitude(tmpLon);
                eboxList[i].setLatitude(tmpLat);

//                System.out.println( eboxList[i].getLatitude() + ", " + eboxList[i].getLongitude());
            }
        return eboxList;
    }

    public static void main(String[] args) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        Ebox[] array = new Ebox[100];
        File inFile = new File("src/DrexelBoxes.xml");
        array = DZ_Eboxes.parseXML(inFile);
        for(int i = 0; i < array.length; i++) {
            System.out.println( array[i].getLatitude() );
            if( array[i].getLatitude() == 0.0 ) {
                break;
            }
        }

    }
}
