This generator is aimed to make pdf documents using an XML-markup and JSON or XML file as a data source.
This thing is still under development.

Simple example of usage:
```java 
@Test
    public void test1() throws IOException, SAXException, ParserConfigurationException, XPathExpressionException, ParseException, DocumentException {
        String markup = getFileContent(new ClassPathResource("test/testmarkup.xml").getFile().getAbsolutePath());

        HashMap<String, byte[]> fontBodies = new HashMap<String, byte[]>();
        fontBodies.put("arial", getFileFontByteContent("arial"));
        
        PropertyGetter getterTest = new PropertyGetterTest2(new ClassPathResource("test/testdata.json").getFile().getPath());
        Object rep = Reporting.CreateDocumentEx(markup, fontBodies, getterTest);
        File f = new File("test.pdf");
        FileOutputStream file = new FileOutputStream(f);
        file.write((byte[]) rep);
    }
```
