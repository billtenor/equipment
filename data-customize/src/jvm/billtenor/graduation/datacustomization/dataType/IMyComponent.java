package billtenor.graduation.datacustomization.dataType;

import billtenor.graduation.datacustomization.fieldTransform.IBaseKeyTransform;

/**
 * Created by lyj on 17-4-13.
 */
public interface IMyComponent {
    public IBaseKeyTransform getKeyTransform();
    public void setKeyTransform(IBaseKeyTransform keyTransform);
}
