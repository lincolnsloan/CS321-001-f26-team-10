package cs321.btree;

/**
 * One node in a B-tree of minimum degree t.
 * Keys are stored in a 0-based array of length 2t - 1.
 * Child pointers are disk byte offsets in a 0-based array of length 2t.
 */
public class BTreeNode {
    int degree;
    TreeObject[] keys;
    long[] children;
    boolean leaf;
    int numKeys;
    long address;

    /**
     * Create a node with room for 2 * degree - 1 keys and 2 * degree children.
     * The new node has no keys. Every child offset starts at 0.
     *
     * @param degree minimum degree t; must be at least 2
     * @param leaf true when this node has no children
     * @throws IllegalArgumentException when degree is less than 2
     */
    public BTreeNode(int degree, boolean leaf) {
        if (degree < 2) {
            throw new IllegalArgumentException("Degree must be at least 2");
        }

        this.degree = degree;
        this.leaf = leaf;
        keys = new TreeObject[2 * degree - 1];
        children = new long[2 * degree];
        numKeys = 0;
        address = 0;
    }

    /**
     * Set this node's location in the B-tree file.
     *
     * @param address byte offset of this node on disk
     */
    public void setAddress(long address) {
        this.address = address;
    }

    /**
     * @return byte offset of this node on disk
     */
    public long getAddress() {
        return address;
    }

    /**
     * Number of bytes this node occupies on disk.
     * The record is the leaf flag (1 byte), numKeys (4 bytes),
     * then (2t - 1) * TreeObject.BYTES key bytes,
     * then 2t * Long.BYTES child-offset bytes.
     * This node's own address is not part of the record.
     *
     * @return fixed size of this node's disk record
     */
    public int getNodeDiskSize() {
        return 1 + Integer.BYTES
            + keys.length * TreeObject.BYTES
            + children.length * Long.BYTES;
    }

    /**
     * @return true when this node has no children
     */
    public boolean isLeaf() {
        return leaf;
    }

    /**
     * Mark this node as a leaf or as an internal node.
     * A split of the root sets the old root to a non-leaf.
     *
     * @param leaf true when this node has no children
     */
    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    /**
     * @return how many keys this node currently holds
     */
    public int getNumKeys() {
        return numKeys;
    }

    /**
     * Set how many keys this node currently holds.
     *
     * @param numKeys key count, from 0 through 2t - 1
     * @throws IllegalArgumentException when numKeys is outside that range
     */
    public void setNumKeys(int numKeys) {
        if (numKeys < 0 || numKeys > keys.length) {
            throw new IllegalArgumentException(
                "numKeys must be from 0 through " + keys.length);
        }
        this.numKeys = numKeys;
    }

    /**
     * @param index 0-based key index, from 0 through 2t - 2
     * @return the TreeObject at index
     * @throws IndexOutOfBoundsException when index is outside the key array
     */
    public TreeObject getKey(int index) {
        return keys[index];
    }

    /**
     * Store a key at a 0-based index.
     *
     * @param index 0-based key index, from 0 through 2t - 2
     * @param key key and frequency to store
     * @throws IndexOutOfBoundsException when index is outside the key array
     */
    public void setKey(int index, TreeObject key) {
        keys[index] = key;
    }

    /**
     * @param index 0-based child index, from 0 through 2t - 1
     * @return disk byte offset of that child, or 0 when the slot is empty
     * @throws IndexOutOfBoundsException when index is outside the child array
     */
    public long getChild(int index) {
        return children[index];
    }

    /**
     * Store a child pointer as a disk byte offset.
     *
     * @param index 0-based child index, from 0 through 2t - 1
     * @param offset byte offset of the child node, or 0 when the slot is empty
     * @throws IndexOutOfBoundsException when index is outside the child array
     */
    public void setChild(int index, long offset) {
        children[index] = offset;
    }
}
