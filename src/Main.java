import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;

public class Main {
    static class Student {
        String name;
        int age;
        Student(String name, int age) { this.name = name; this.age = age; }
        public String dataToString() { return name + " (" + age + ")"; }
    }

    public static void main(String[] args) throws Exception {

        ArrayList<Student> initialStudents = new ArrayList<>();
        initialStudents.add(new Student("Вася", 21));
        initialStudents.add(new Student("Петя", 22));
        initialStudents.add(new Student("Кеша", 23));

        Document xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element rootNode = xmlDocument.createElement("students");
        xmlDocument.appendChild(rootNode);
        for (Student stud : initialStudents) {
            Element studentNode = xmlDocument.createElement("student");
            Element nameNode = xmlDocument.createElement("name");
            nameNode.setTextContent(stud.name);
            Element ageNode = xmlDocument.createElement("age");
            ageNode.setTextContent(String.valueOf(stud.age));
            studentNode.appendChild(nameNode);
            studentNode.appendChild(ageNode);
            rootNode.appendChild(studentNode);
        }
        Transformer xmlWriter = TransformerFactory.newInstance().newTransformer();
        xmlWriter.setOutputProperty(OutputKeys.INDENT, "yes");
        xmlWriter.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        xmlWriter.transform(new DOMSource(xmlDocument), new StreamResult(new File("students.xml")));

        ArrayList<Student> parsedStudents = new ArrayList<>();
        SAXParser saxEngine = SAXParserFactory.newInstance().newSAXParser();
        saxEngine.parse(new File("students.xml"), new DefaultHandler() {
            String activeTag;
            String studentName;
            int studentAge;
            public void startElement(String uri, String localName, String qName, Attributes attrs) {
                activeTag = qName;
            }
            public void characters(char[] ch, int start, int length) {
                String textChunk = new String(ch, start, length).trim();
                if (textChunk.isEmpty()) return;
                if ("name".equals(activeTag)) studentName = textChunk;
                else if ("age".equals(activeTag)) studentAge = Integer.parseInt(textChunk);
            }
            public void endElement(String uri, String localName, String qName) {
                if ("student".equals(qName)) parsedStudents.add(new Student(studentName, studentAge));
                activeTag = null;
            }
        });
        for (Student stud : parsedStudents) System.out.println(stud.dataToString());
    }
}