import java.io.File;

/**
 * Created by max on 02.02.16.
 */
public class CustomItem {
    String viewName;
    File file;

    public CustomItem(String viewName, File file) {
        this.viewName = viewName;
        this.file = file;
    }

    @Override
    public String toString() {
        return viewName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomItem that = (CustomItem) o;

        if (viewName != null ? !viewName.equals(that.viewName) : that.viewName != null) return false;
        return !(file != null ? !file.equals(that.file) : that.file != null);

    }

    @Override
    public int hashCode() {
        int result = viewName != null ? viewName.hashCode() : 0;
        result = 31 * result + (file != null ? file.hashCode() : 0);
        return result;
    }

    public String getViewName() {

        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
