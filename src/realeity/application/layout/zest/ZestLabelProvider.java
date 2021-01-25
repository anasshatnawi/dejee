package realeity.application.layout.zest;
/**
 * This code is inspired from http://www.vogella.com/tutorials/EclipseZest/article.html and 
 * from http://stackoverflow.com/questions/3068073/how-to-draw-directed-edges-with-igraphcontentprovider .
 */


import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.gef4.zest.core.viewers.EntityConnectionData;

public class ZestLabelProvider extends LabelProvider {
    //final Image image = Display.getDefault().getSystemImage(SWT.ICON_WARNING);

  @Override
  public String getText(Object element) {
    if (element instanceof MyNode) {
      MyNode myNode = (MyNode) element;
      return myNode.getName();
    }
    // Not called with the IGraphEntityContentProvider
    if (element instanceof MyConnection) {
      MyConnection myConnection = (MyConnection) element;
      return myConnection.getLabel();
    }

    if (element instanceof EntityConnectionData) {
      EntityConnectionData test = (EntityConnectionData) element;
      return "";
    }
    throw new RuntimeException("Wrong type: "
                               + element.getClass().toString());
  }
  
 /* @Override
  public Image getImage(Object element) {//http://stackoverflow.com/questions/3068073/how-to-draw-directed-edges-with-igraphcontentprovider
	  if (element instanceof MyNode) {
	      MyNode myNode = (MyNode) element;
	      return image;
	    }
      return null;
  }*/
} 
