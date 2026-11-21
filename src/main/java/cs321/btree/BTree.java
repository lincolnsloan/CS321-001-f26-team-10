package cs321.btree;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class BTree implements BTreeInterface
{
    private String filename;
    private File file;
    private int degree;
    private long rootAddress;
    private BTreeNode root;
    private long size;
    private long numNodes;
    private int height;
    
    private RandomAccessFile dataFile;
    private FileChannel fileChannel;
    private long nextDiskAddress;
    
    private static final int METADATA_SIZE = Long.BYTES;
    
    private boolean useCache;
    private int cacheSize;

    /* Constructor for creating a new BTree */
    public BTree(String filename) throws BTreeException, IOException {
        this(0, filename, false, 0);
    }

    /* Constructor for creating a new BTree with a specified degree */
    public BTree(int degree, String filename) throws BTreeException, IOException {
        this(degree, filename, false, 0);
    }

    /* Constructor for creating a new BTree with caching enabled */
    public BTree(int degree, String filename, boolean useCache, int cacheSize) throws BTreeException, IOException {
        this.filename = filename;
        this.file = new File(filename);
        this.useCache = useCache;
        this.cacheSize = cacheSize;

        if (degree == 0) {
            this.degree = 100;
        } else {
            this.degree = degree;
        }

        try {
            if (!file.exists()) {
                file.createNewFile();
                dataFile = new RandomAccessFile(file, "rw");
                fileChannel = dataFile.getChannel();

                nextDiskAddress = METADATA_SIZE;
                rootAddress = nextDiskAddress;

                root = new BTreeNode(this.degree, true);
                root.setAddress(rootAddress);
                nextDiskAddress += root.getNodeDiskSize();
                size = 0;
                numNodes = 1;
                height = 0;

                writeMetadata();
                diskWrite(root);
            } else {
                dataFile = new RandomAccessFile(file, "rw");
                fileChannel = dataFile.getChannel();
                readMetadata();
                root = diskRead(rootAddress);
            }
        } catch (IOException e) {
            throw new BTreeException("Failed to initialize BTree file: " + e.getMessage());
        }
    }

   private void diskWrite(BTreeNode node) throws IOException {
        if (node == null) {
            return;
        }

        fileChannel.position(node.getAddress());

        int nodeByteSize = BTreeNode.calculateByteSize(degree);
        ByteBuffer buffer = ByteBuffer.allocate(nodeByteSize);

        buffer.put((byte) (node.isLeaf() ? 1 : 0));
        buffer.putInt(node.getNumKeys());

        TreeObject[] keys = node.getKeys();
        for (int i = 0; i < (2 * degree - 1); i++) {
            String keyStr = "";
            long frequency = 0L;

            if (keys != null && i < node.getNumKeys() && keys[i] != null) {
                keyStr = keys[i].getKey();
                frequency = keys[i].getCount();
            }

            byte[] rawBytes = keyStr.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            byte[] keyBytes = new byte[64];
            System.arraycopy(rawBytes, 0, keyBytes, 0, Math.min(rawBytes.length, 64));

            buffer.put(keyBytes);
            buffer.putLong(frequency);
        }

        long[] children = node.getChildren();
        for (int i = 0; i < (2 * degree); i++) {
            long childAddress = (children != null) ? children[i] : 0L;
            buffer.putLong(childAddress);
        }

        buffer.flip();
        while (buffer.hasRemaining()) {
            fileChannel.write(buffer);
        }
    }

<<<<<<< HEAD
    private void readMetadata() throws IOException {
        fileChannel.position(0);
        ByteBuffer buffer = ByteBuffer.allocate(METADATA_SIZE);
        while (buffer.hasRemaining()) {
            if (fileChannel.read(buffer) == -1) {
                break;
            }
        }
        buffer.flip();
        if (buffer.remaining() >= Long.BYTES) {
            rootAddress = buffer.getLong();
        }
=======
    private void diskWrite(BTreeNode node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'diskWrite'");
    }

    private void readMetadata() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'readMetadata'");
>>>>>>> 948373ec6edfc77d2fe9c18b8c741b161377ab97
    }

    private void writeMetadata() throws IOException {
        fileChannel.position(0);
        ByteBuffer buffer = ByteBuffer.allocate(METADATA_SIZE);
        buffer.putLong(rootAddress);
        buffer.flip();
        while (buffer.hasRemaining()) {
            fileChannel.write(buffer);
        }
    }
    @Override
    public long getSize() {
        return size;
    }

    @Override
    public int getDegree() {
        return degree;
    }

    @Override
    public long getNumberOfNodes() {
        return numNodes;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void insert(TreeObject obj) throws IOException {
        int found = findKey(root, obj);
        if (found >= 0) {
            root.getKey(found).incCount();
        } else if (isFull(root)) {
            BTreeNode oldRoot = root;
            root = new BTreeNode(degree, false);
            root.setAddress(nextDiskAddress);
            nextDiskAddress += root.getNodeDiskSize();
            numNodes++;
            height++;
            root.setChild(0, oldRoot.getAddress());
            rootAddress = root.getAddress();
            splitChild(root, 0);
            insertNonFull(root, obj);
        } else {
            insertNonFull(root, obj);
        }
        diskWrite(root);
    }

    /* Split the full child at childIndex. parent is not full. */
    private void splitChild(BTreeNode parent, int childIndex) throws IOException {
        int t = degree;
        BTreeNode full = diskRead(parent.getChild(childIndex));
        BTreeNode sibling = new BTreeNode(degree, full.isLeaf());
        sibling.setAddress(nextDiskAddress);
        nextDiskAddress += sibling.getNodeDiskSize();
        numNodes++;

        for (int j = 0; j < t - 1; j++) {
            sibling.setKey(j, full.getKey(j + t));
        }
        if (!full.isLeaf()) {
            for (int j = 0; j < t; j++) {
                sibling.setChild(j, full.getChild(j + t));
            }
        }
        TreeObject median = full.getKey(t - 1);
        full.setNumKeys(t - 1);
        sibling.setNumKeys(t - 1);

        for (int j = parent.getNumKeys(); j >= childIndex + 1; j--) {
            parent.setChild(j + 1, parent.getChild(j));
        }
        parent.setChild(childIndex + 1, sibling.getAddress());

        for (int j = parent.getNumKeys() - 1; j >= childIndex; j--) {
            parent.setKey(j + 1, parent.getKey(j));
        }
        parent.setKey(childIndex, median);
        parent.setNumKeys(parent.getNumKeys() + 1);

        diskWrite(full);
        diskWrite(sibling);
        if (parent != root) {
            diskWrite(parent);
        }
    }

    /* Insert obj into node, which is not full. Called by insert(). */
    private void insertNonFull(BTreeNode node, TreeObject obj) throws IOException {
        int found = findKey(node, obj);
        if (found >= 0) {
            node.getKey(found).incCount();
            if (node != root) {
                diskWrite(node);
            }
            return;
        }

        if (node.isLeaf()) {
            int i = node.getNumKeys() - 1;
            while (i >= 0 && obj.compareTo(node.getKey(i)) < 0) {
                node.setKey(i + 1, node.getKey(i));
                i--;
            }
            node.setKey(i + 1, obj);
            node.setNumKeys(node.getNumKeys() + 1);
            size++;
            if (node != root) {
                diskWrite(node);
            }
            return;
        }

        int i = node.getNumKeys() - 1;
        while (i >= 0 && obj.compareTo(node.getKey(i)) < 0) {
            i--;
        }
        i++;

        BTreeNode child = diskRead(node.getChild(i));
        int childFound = findKey(child, obj);
        if (childFound >= 0) {
            child.getKey(childFound).incCount();
            diskWrite(child);
            return;
        }
        if (isFull(child)) {
            splitChild(node, i);
            if (obj.compareTo(node.getKey(i)) == 0) {
                node.getKey(i).incCount();
                if (node != root) {
                    diskWrite(node);
                }
                return;
            }
            if (obj.compareTo(node.getKey(i)) > 0) {
                i++;
            }
            child = diskRead(node.getChild(i));
        }
        insertNonFull(child, obj);
    }

    private int findKey(BTreeNode node, TreeObject obj) {
        for (int i = 0; i < node.getNumKeys(); i++) {
            if (node.getKey(i).compareTo(obj) == 0) {
                return i;
            }
        }
        return -1;
    }

    private boolean isFull(BTreeNode node) {
        return node.getNumKeys() == 2 * degree - 1;
    }

    @Override
    public void dumpToFile(PrintWriter out) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'dumpToFile'");
    }

    @Override
    public void dumpToDatabase(String dbName, String tableName) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'dumpToDatabase'");
    }

    @Override
    public TreeObject search(String key) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'search'");
    }

    @Override
    public void delete(String key) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }
}