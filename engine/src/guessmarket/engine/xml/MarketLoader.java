package guessmarket.engine.xml;

import guessmarket.engine.exception.InvalidFileException;
import guessmarket.engine.method.LmsrMethod;
import guessmarket.engine.model.CommissionType;
import guessmarket.engine.model.Event;
import guessmarket.engine.model.EventOption;
import guessmarket.engine.model.Market;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MarketLoader {
    private static final String XML_SUFFIX = ".xml";
    private static final String ROOT_TAG = "Guess-Market";
    private static final String EVENTS_TAG = "GM-events";
    private static final String EVENT_TAG = "GM-event";
    private static final String NAME_ATTRIBUTE = "name";
    private static final String ID_TAG = "id";
    private static final String DESCRIPTION_TAG = "description";
    private static final String COMMISSION_TAG = "comision";
    private static final String TYPE_ATTRIBUTE = "type";
    private static final String OPTIONS_TAG = "GM-options";
    private static final String OPTION_TAG = "GM-option";
    private static final String METHOD_TAG = "GM-method";
    private static final String LMSR_TAG = "GM-LMSR";
    private static final String B_TAG = "b";

    private static final int MIN_COMMISSION = 0;
    private static final int MAX_COMMISSION = 90;
    private static final int OPTIONS_PER_EVENT = 2;

    public Market load(String path) throws InvalidFileException 
    {
        File file = checkFile(path);
        Element root = readRoot(file);
        return buildMarket(root);
    }

    private File checkFile(String path) throws InvalidFileException
    {
        if (path.isEmpty()) 
        {
            throw new InvalidFileException("No path was entered");
        }
        if (!path.toLowerCase().endsWith(XML_SUFFIX))
        {
            throw new InvalidFileException("The file '" + path + "' is not an xml file - the path must end with " + XML_SUFFIX);
        }

        File file = new File(path);
        if (!file.exists())
        {
            throw new InvalidFileException("There is no file at the path '" + path + "'");
        }
        if (!file.isFile()) 
        {
            throw new InvalidFileException("The path '" + path + "' points to a folder and not to a file");
        }
        return file;
    }

    private Element readRoot(File file) throws InvalidFileException
    {
        Document document;
        try 
            {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(file);
        } catch (ParserConfigurationException | SAXException | IOException e) 
        {
            throw new InvalidFileException("The file could not be read as an xml document: " + e.getMessage());
        }

        Element root = document.getDocumentElement();
        if (root == null || !ROOT_TAG.equals(root.getNodeName()))
        {
            throw new InvalidFileException("The main element of the file should be '" + ROOT_TAG + "'");
        }
        return root;
    }

    private Market buildMarket(Element root) throws InvalidFileException
    {
        Element eventsElement = findChild(root, EVENTS_TAG);
        if (eventsElement == null) {
            throw new InvalidFileException("The element '" + EVENTS_TAG + "' is missing from the file");
        }

        List<Element> eventElements = findChildren(eventsElement, EVENT_TAG);
        if (eventElements.isEmpty()) 
        {
            throw new InvalidFileException("The file does not contain any event");
        }

        Market market = new Market();
        for (Element eventElement : eventElements)
            {
            Event event = buildEvent(eventElement);
            if (market.containsId(event.getId())) 
            {
                throw new InvalidFileException("Event number " + event.getId() + " appears more than once in the file. Every event must have its own unique number");
            }
            market.addEvent(event);
        }
        return market;
    }

    private Event buildEvent(Element eventElement) throws InvalidFileException 
    {
        String name = eventElement.getAttribute(NAME_ATTRIBUTE).trim();
        if (name.isEmpty()) 
        {
            throw new InvalidFileException("One of the events has no name");
        }

        int id = readNumber(eventElement, ID_TAG, "the number of the event '" + name + "'");
        String description = readText(eventElement, DESCRIPTION_TAG, name);
        int commission = readCommission(eventElement, name);
        CommissionType commissionType = readCommissionType(eventElement, name);
        List<EventOption> options = readOptions(eventElement, name);
        int b = readLiquidity(eventElement, name);

        return new Event(id, name, description, commission, commissionType, options, new LmsrMethod(options, b));
    }

    private int readCommission(Element eventElement, String eventName) throws InvalidFileException
    {
        int commission = readNumber(eventElement, COMMISSION_TAG, "the commission of the event '" + eventName + "'");
        if (commission < MIN_COMMISSION || commission > MAX_COMMISSION) 
        {
            throw new InvalidFileException("The commission of the event '" + eventName + "' is " + commission
                    + ", but it has to be between " + MIN_COMMISSION + " and " + MAX_COMMISSION + ".");
        }
        return commission;
    }

    private CommissionType readCommissionType(Element eventElement, String eventName) throws InvalidFileException 
    {
        Element commissionElement = findChild(eventElement, COMMISSION_TAG);
        String value = commissionElement.getAttribute(TYPE_ATTRIBUTE).trim();
        CommissionType type = CommissionType.fromFileValue(value);
        if (type == null) {
            throw new InvalidFileException("The commission type of the event '" + eventName + "' is '" + value
                    + "', but only 'on-purchase' or 'on-close' are allowed");
        }
        return type;
    }

    private List<EventOption> readOptions(Element eventElement, String eventName) throws InvalidFileException
    {
        Element optionsElement = findChild(eventElement, OPTIONS_TAG);
        if (optionsElement == null) {
            throw new InvalidFileException("The event '" + eventName + "' has no options");
        }

        List<Element> optionElements = findChildren(optionsElement, OPTION_TAG);
        if (optionElements.size() != OPTIONS_PER_EVENT) 
        {
            throw new InvalidFileException("The event '" + eventName + "' has " + optionElements.size() + " options, but every event must have exactly " + OPTIONS_PER_EVENT + " options");
        }

        List<EventOption> options = new ArrayList<EventOption>();
        for (Element optionElement : optionElements) 
        {
            String optionName = optionElement.getTextContent().trim();
            if (optionName.isEmpty()) 
            {
                throw new InvalidFileException("The event '" + eventName + "' has an option without a name");
            }
            for (EventOption option : options)
                {
                if (option.getName().equalsIgnoreCase(optionName))
                {
                    throw new InvalidFileException("The event '" + eventName + "' has two options named '" + optionName + "'");
                }
            }
            options.add(new EventOption(optionName));
        }
        return options;
    }

    private int readLiquidity(Element eventElement, String eventName) throws InvalidFileException
    {
        Element methodElement = findChild(eventElement, METHOD_TAG);
        if (methodElement == null)
        {
            throw new InvalidFileException("The event '" + eventName + "' has no trading method");
        }

        Element lmsrElement = findChild(methodElement, LMSR_TAG);
        if (lmsrElement == null) 
        {
            throw new InvalidFileException("The trading method of the event '" + eventName + "' is not " + LMSR_TAG + ", and it is the only method that is supported");
        }

        int b = readNumber(lmsrElement, B_TAG, "the liquidity (b) of the event '" + eventName + "'");
        if (b <= 0) 
        {
            throw new InvalidFileException("The liquidity (b) of the event '" + eventName + "' is " + b
                    + ", but it has to be a positive number.");
        }
        return b;
    }

    private String readText(Element parent, String tagName, String eventName) throws InvalidFileException 
    {
        Element element = findChild(parent, tagName);
        if (element == null)
        {
            throw new InvalidFileException("The element '" + tagName + "' is missing from the event '" + eventName + "'.");
        }
        return element.getTextContent().trim();
    }

    private int readNumber(Element parent, String tagName, String fieldDescription) throws InvalidFileException
    {
        Element element = findChild(parent, tagName);
        if (element == null) 
        {
            throw new InvalidFileException("The element '" + tagName + "' is missing (" + fieldDescription + ")");
        }

        String value = element.getTextContent().trim();
        try 
            {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) 
            {
            throw new InvalidFileException("The value of " + fieldDescription + " is '" + value + "' and it is not a whole number");
        }
    }

    private Element findChild(Element parent, String tagName)
    {
        List<Element> children = findChildren(parent, tagName);
        if (children.isEmpty())
        {
            return null;
        }
        return children.get(0);
    }

    private List<Element> findChildren(Element parent, String tagName) 
    {
        List<Element> children = new ArrayList<Element>();
        NodeList nodes = parent.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++)
            {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals(tagName)) 
            {
                children.add((Element) node);
            }
        }
        return children;
    }
}
