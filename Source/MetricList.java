package Source;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MetricList implements List<Metric>
{
    private int mSize = 0;
    private int mCapacity = 0;
    private Metric[] mMetricArray = null;

    public MetricList() {}

    public MetricList(int reserve) {
        mMetricArray = new Metric[reserve];
        mCapacity = reserve;
    }

    @Override
    public int size() {
        return mSize;
    }

    @Override
    public boolean isEmpty() {
        return mSize == 0;
    }

    @Override
    public boolean contains(Object o) {
        for (int i = 0; i < mSize; ++i)
            if (mMetricArray[i] == o)
                return true;
        return false;
    }

    @Override
    public Iterator iterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object[] toArray() {
        return mMetricArray;
    }

    @Override
    public Object[] toArray(Object[] a) {
        return a;
    }

    @Override
    public boolean add(Metric e) {
        // if array is full, increase the capacity
        if (mSize == mCapacity) {
            // set initial capacity to only contain 1 object
            if (mCapacity == 0) mMetricArray = new Metric[1];
            // increase capacity by factor of 2
            else                resizeCapacity(mCapacity * 2);
        }
        // insert the object at the end
        mMetricArray[mSize++] = (Metric) e;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        for (int i = 0; i < mSize; ++i)
            if (mMetricArray[i] == o)
                return true;
        return false;
    }

    @Override
    public boolean containsAll(Collection c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean addAll(Collection c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean addAll(int index, Collection c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean removeAll(Collection c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean retainAll(Collection c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void clear() {
        // empty all values
        for (int i = 0; i < mSize; ++i)
            mMetricArray[i] = null;
        mSize = 0;
    }

    @Override
    public Metric get(int index) {
        if (index >= 0 && index < mSize)
            return mMetricArray[index];
        return null;
    }

    @Override
    public Metric set(int index, Metric element) {
        Metric obj = null;
        if (index >= 0 && index < mSize) {
            obj = mMetricArray[index];
            mMetricArray[index] = element;
        }
        return obj;
    }

    @Override
    public void add(int index, Metric element) {
        // if insert at index greater than capacity
        // resize capacity to be able to store 'to' the specified index
        if (index > mCapacity) {
            resizeCapacity(index + 1);
            mSize = mCapacity;
            // insert object as specified
            mMetricArray[index] = element;
            return;
        }
        // increase the capacity first if array is full
        if (mSize == mCapacity)
            resizeCapacity(mCapacity + 1);
        // move trailing object to expand 1 block (create a gap)
        for (int i = mSize; i > index + 1; --i)
            mMetricArray[i] = mMetricArray[i - 1];
        // insert object as specified
        mMetricArray[index] = element;
        ++mSize;
    }

    @Override
    public Metric remove(int index) {
        Metric obj = null;
        if (index >= 0 && index < mSize) {
            obj = mMetricArray[index];
            // move trailing object to fill the gap
            for (int i = index; i < mSize - 1; ++i)
                mMetricArray[i] = mMetricArray[i + 1];
            mMetricArray[--mSize] = null;
        }
        return obj;
    }

    @Override
    public int indexOf(Object o) {
        for (int i = 0; i < mSize; ++i)
            if (mMetricArray[i] == o)
                return i;
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ListIterator listIterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListIterator listIterator(int index) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List subList(int fromIndex, int toIndex) {
        // TODO Auto-generated method stub
        return null;
    }

    private void resizeCapacity(int newCapacity) {
        if (newCapacity == mCapacity) return;
        // create new contiguous array
        Metric[] temp = new Metric[newCapacity];
        // decrease capacity
        // only if previous size is greater than new capacity
        // only fill the object where fits in the new capacity
        if (mCapacity > newCapacity && mSize > newCapacity)
            mSize = newCapacity;
        // create a copy from previous array
        for (int i = 0; i < mSize; ++i)
            temp[i] = mMetricArray[i];
        // replace the old array into a new ones
        mMetricArray = temp;
        // update new capacity
        mCapacity = newCapacity;
    }
}
