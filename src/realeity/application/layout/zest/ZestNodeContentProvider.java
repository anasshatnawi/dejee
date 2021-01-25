package realeity.application.layout.zest;
/**
 * This code comes from http://www.vogella.com/tutorials/EclipseZest/article.html
 *
 */
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.gef4.zest.core.viewers.IGraphEntityContentProvider;


public class ZestNodeContentProvider extends ArrayContentProvider  
                                     implements IGraphEntityContentProvider {

  @Override
  public Object[] getConnectedTo(Object entity) {
    if (entity instanceof MyNode) {
      MyNode node = (MyNode) entity;
      return node.getConnectedTo().toArray();
    }
    throw new RuntimeException("Type not supported");
  }
} 