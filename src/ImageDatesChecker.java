import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;

/**
 * <pre>
 * <B>Copyright:</B>   HP Software IL
 * <B>Owner:</B>       <a href="mailto:izik.golan@hp.com">Izik Golan</a>
 * <B>Creation:</B>    03/05/2015 20:21
 * <B>Since:</B>       BSM 9.21
 * <B>Description:</B>
 *
 * </pre>
 */
public class ImageDatesChecker {

  public static void main(String[] args) {
    try {
      final Path path = Paths.get("C:\\Users\\golaniz\\Desktop\\2014-02");
      Files.walkFileTree(path, new ImageDateCheckVisitor());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  private static class ImageDateCheckVisitor extends SimpleFileVisitor<Path> {

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
      if (!attrs.isDirectory()) {
        final String fileName = file.toAbsolutePath().toString();
        if (fileName.endsWith(".jpg")) {
          final ImageDate imageDate = readImageDate(fileName);
          if (imageDate != null) {
            System.out.println(imageDate);
          }

        }
      }
      return FileVisitResult.CONTINUE;
    }
  }

  private static ImageDate readImageDate(String fileName)  {
    final ImageDate imageDate = new ImageDate(fileName);
    final File file = new File(fileName);
    final Metadata metadata;
    try {
      metadata = JpegMetadataReader.readMetadata(file);
    } catch (JpegProcessingException e) {
      //e.printStackTrace();
      return null;
    }

    Iterator directoryIterator = metadata.getDirectoryIterator();
    while(directoryIterator.hasNext()) {
      Directory directory = (Directory) directoryIterator.next();
      Iterator tagIterator = directory.getTagIterator();
      while (tagIterator.hasNext()) {
        Tag tag = (Tag) tagIterator.next();
        if ("Date/Time".equals(tag.getTagName())  ) {
          try {
            imageDate.setDateTime( tag.getDescription() );
          } catch (MetadataException e) {
            e.printStackTrace();
            imageDate.setDateTime ( null );
          }
        }
        else if ("Date/Time Original".equals(tag.getTagName())  ) {
          try {
            imageDate.setOriginalDateTime(tag.getDescription());
          } catch (MetadataException e) {
            e.printStackTrace();
            imageDate.setOriginalDateTime( null );
          }
        }
        else if ( "Date/Time Digitized".equals(tag.getTagName()) ) {
          try {
            imageDate.setDigitizedDateTime(tag.getDescription());
          } catch (MetadataException e) {
            e.printStackTrace();
            imageDate.setDigitizedDateTime( null );
          }
        }
      }
    }
    return imageDate;
  }

  private static class ImageDate {
    private final String fileName;
    private       String dateTime;
    private       String originalDateTime;
    private       String digitizedDateTime;

    private ImageDate(String fileName) {this.fileName = fileName;}

    public String getFileName() {
      return fileName;
    }

    public String getDateTime() {
      return dateTime;
    }

    public void setDateTime(String dateTime) {
      this.dateTime = dateTime;
    }

    public String getOriginalDateTime() {
      return originalDateTime;
    }

    public void setOriginalDateTime(String originalDateTime) {
      this.originalDateTime = originalDateTime;
    }

    public String getDigitizedDateTime() {
      return digitizedDateTime;
    }

    public void setDigitizedDateTime(String digitizedDateTime) {
      this.digitizedDateTime = digitizedDateTime;
    }

    public boolean ok() { return dateTime.equals(originalDateTime) && originalDateTime.equals(digitizedDateTime); }

    @Override
    public String toString() {
      return new StringBuilder("<Image" + " ok=\""+ok()+"\"" + " fileName=\""+fileName+"\"" + " dateTime=\""+dateTime+"\"" + " originalDateTime=\""+originalDateTime+"\"" + " digitizedDateTime=\""+digitizedDateTime+"\"" ).toString();
    }
  }

}
