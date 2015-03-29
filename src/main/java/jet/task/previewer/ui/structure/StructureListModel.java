package jet.task.previewer.ui.structure;

import jet.task.previewer.model.Entry;
import org.jetbrains.annotations.NotNull;

import javax.swing.AbstractListModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex Koshevoy on 28.03.2015.
 */
public class StructureListModel extends AbstractListModel<Entry> {
    private final List<Entry> content;

    public StructureListModel() {
        this.content = new ArrayList<>();
    }

    public StructureListModel(@NotNull List<Entry> content) {
        this.content = new ArrayList<>(content);
    }

    public void updateContent(@NotNull List<Entry> content) {
        // todo is this a good idea to call two fireInterval...s()?
        int oldContentSize = this.content.size();
        this.content.clear();
        fireIntervalRemoved(this, 0, oldContentSize);
        this.content.addAll(content);
        fireIntervalAdded(this, 0, this.content.size());
    }

    @Override
    public int getSize() {
        return content.size();
    }

    @Override
    public Entry getElementAt(int index) {
        return content.get(index);
    }
}
