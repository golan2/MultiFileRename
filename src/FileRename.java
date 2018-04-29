import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifDirectory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

;

/**
 * <pre>
 * <B>Copyright:</B>   Izik Golan
 * <B>Owner:</B>       <a href="mailto:golan2@hotmail.com">Izik Golan</a>
 * <B>Creation:</B>    02/05/2015 22:44
 * <B>Since:</B>       BSM 9.21
 * <B>Description:</B>
 *
 * </pre>
 */
public class FileRename {

  public static void main(String[] args) throws IOException, JpegProcessingException, MetadataException {

    //System.out.println(RenameFileVisitor.calcNewFileName("20140627_085107_14.jpg"));

    final String fileName = "C:\\Users\\golaniz\\Desktop\\TestFileRename\\2014-03-23 07.45.51.jpg";

    final File file = new File(fileName);
    final Metadata metadata = JpegMetadataReader.readMetadata(file);

    printAllMetadata(metadata);


  }


  private static void printAllMetadata(Metadata metadata) {
    Iterator directoryIterator = metadata.getDirectoryIterator();
    while(directoryIterator.hasNext()) {
      Directory directory = (Directory)directoryIterator.next();
      Iterator tagIterator = directory.getTagIterator();
      System.out.println("["+directory.getName()+"]");
      while(tagIterator.hasNext()) {
        Tag tag = (Tag)tagIterator.next();

        try {
          System.out.println("\t" + tag.getTagName() + " = " + tag.getDescription());
        } catch (MetadataException var7) {
          System.err.println(var7.getMessage());
          System.err.println(tag.getDirectoryName() + " " + tag.getTagName() + " (error)");
        }
      }

      if(directory.hasErrors()) {
        Iterator var10 = directory.getErrors();

        while(var10.hasNext()) {
          System.out.println("ERROR: " + var10.next());
        }
      }
    }
  }

  private static void nodeToString(Node node, StringBuilder buf) {
    buf.append("<").append(node.getNodeName());
    final NamedNodeMap attributes = node.getAttributes();
    for (int i = 0; i < attributes.getLength(); i++) {
      final Node item = attributes.item(i);
      buf.append(" ").append(item.getNodeName()).append("=\"").append(item.getNodeValue()).append("\"");
    }
    buf.append(">");
    final String nodeValue = node.getNodeValue();
    if (nodeValue!=null) {
      buf.append(nodeValue);
    }

    final Node firstChild = node.getFirstChild();
    if (firstChild!=null) {
      nodeToString(firstChild, buf);
    }

    buf.append("</").append(node.getNodeName()).append(">");

    final Node nextSibling = node.getNextSibling();
    if (nextSibling!=null) {
      nodeToString(nextSibling, buf);
    }

  }

  private static class RenameFileVisitor extends SimpleFileVisitor<Path> {
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
      if (!attrs.isDirectory()) {
        final Path folder = file.getParent();
        final String fileName = file.getFileName().toString();

        //Metadata metadata = ImageMetadataReader.readMetadata
        //System.out.println(fileName + " |C " + attrs.creationTime() + " |M " + attrs.lastModifiedTime());

        //
        //if (fileNameMatches(fileName)) {
        //  final String newFileName = calcNewFileName(fileName);
        //
        //  final Path newFile = Paths.get(folder.toString() + "\\" + newFileName);
        //
        //  System.out.println(file);
        //  System.out.println(newFile);
        //
        //  Files.move(file, newFile);
        //}
      }
      return FileVisitResult.CONTINUE;
    }

    private static boolean fileNameMatches(String fileName) {
      return (isNumeric(fileName.substring(0,8)) && fileName.substring(8,9).equals("_"));
    }

    private static boolean isNumeric(String str) {
      try
      {
        //noinspection ResultOfMethodCallIgnored
        Double.parseDouble(str);
      }
      catch(NumberFormatException ignored)
      {
        return false;
      }
      return true;
    }

    //0         1         2
    //012345678901234567890123
    //20140120_214357-1.jpg

    private static String calcNewFileName(String oldName) {
      final StringBuilder result = new StringBuilder();
      result.append(oldName.substring(0,4));    //year
      result.append("-");
      result.append(oldName.substring(4,6));    //month
      result.append("-");
      result.append(oldName.substring(6,8));    //day
      result.append(" ");
      result.append(oldName.substring(9,11));    //hour
      result.append(".");
      result.append(oldName.substring(11,13));   //minute
      result.append(".");
      result.append(oldName.substring(13,15));   //second

      if (oldName.substring(15,16).equals("_")) {
        result.append("-");
        result.append(oldName.substring(16));
      }
      else {
        result.append(oldName.substring(15));
      }

      return result.toString();



    }
  }
}
