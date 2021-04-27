package BPTree;

import java.io.Serializable;

public class BPTreeInnerNode<T extends Comparable<T>> extends BPTreeNode<T>  implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BPTreeNode<T>[] children;
	/**
	 * create BPTreeNode given order.
	 * @param n
	 */
	@SuppressWarnings("unchecked")
	public BPTreeInnerNode(int n) 
	{
		super(n);
		keys = new Comparable[n];
		children = new BPTreeNode[n+1];
	}

	/**
	 * get child with specified index
	 * @return Node which is child at specified index
	 */
	public BPTreeNode<T> getChild(int index) 
	{
		return children[index];
	}
	
	/**
	 * creating child at specified index
	 */
	public void setChild(int index, BPTreeNode<T> child) 
	{
		children[index] = child;
	}
	/**
	 * get the first child of this node.
	 * @return first child node.
	 */
	public BPTreeNode<T> getFirstChild()
	{
		return children[0];
	}
	/**
	 * get the last child of this node
	 * @return last child node.
	 */
	public BPTreeNode<T> getLastChild()
	{
		return children[numberOfKeys];
	}
	/**
	 * @return the minimum keys values in InnerNode
	 */
	public int minKeys()
	{
		if(this.isRoot())
			return 1;
		return (order + 2) / 2 - 1;
	}
	/**
	 * insert given key in the corresponding index.
	 * @param key key to be inserted
	 * @param Ref reference which that inserted key is located
	 * @param parent parent of that inserted node
	 * @param ptr index of pointer in the parent node pointing to the current node
	 * @return value to be pushed up to the parent.
	 */
	public PushUp<T> insert(T key, Ref recordReference, BPTreeInnerNode<T> parent, int ptr)
	{
		int index = findIndex(key);
		PushUp<T> pushUp = children[index].insert(key, recordReference, this, index);
		
		if(pushUp == null)
			return null;
		
		if(this.isFull())
		{
			BPTreeInnerNode<T> newNode = this.split(pushUp);
			Comparable<T> newKey = newNode.getFirstKey();
			newNode.deleteAt(0, 0);
			return new PushUp<T>(newNode, newKey);
		}
		else
		{
			index = 0;
			while (index < numberOfKeys && getKey(index).compareTo(key) < 0)
				++index;
			this.insertRightAt(index, pushUp.key, pushUp.newNode);
			return null;
		}
	}
	/**
	 * split the inner node and adjust values and pointers.
	 * @param pushup key to be pushed up to the parent in case of splitting.
	 * @return Inner node after splitting
	 */
	@SuppressWarnings("unchecked")
	public BPTreeInnerNode<T> split(PushUp<T> pushup) 
	{
		int keyIndex = this.findIndex((T)pushup.key);
		int midIndex = numberOfKeys / 2 - 1;
		if(keyIndex > midIndex)				//split nodes evenly
			++midIndex;		

		int totalKeys = numberOfKeys + 1;
		//move keys to a new node
		BPTreeInnerNode<T> newNode = new BPTreeInnerNode<T>(order);
		for (int i = midIndex; i < totalKeys - 1; ++i) 
		{	
			newNode.insertRightAt(i - midIndex, this.getKey(i), this.getChild(i+1));
			numberOfKeys--;
		}
		newNode.setChild(0, this.getChild(midIndex));
		
		//insert the new key
//		System.out.println(midIndex);
		if(keyIndex < totalKeys / 2)
			this.insertRightAt(keyIndex, pushup.key, pushup.newNode);
		else
			newNode.insertRightAt(keyIndex - midIndex, pushup.key, pushup.newNode);
		

		return newNode;
	}
	/**
	 * find the correct place index of specified key in that node.
	 * @param key to be looked for
	 * @return index of that given key
	 */
	public int findIndex(T key) 
	{
		for (int i = 0; i < numberOfKeys; ++i) 
		{
			int cmp = getKey(i).compareTo(key);
			if (cmp > 0)
				return i;
		}
		return numberOfKeys;
	}
	/**
	 * insert at given index a given key
	 * @param index where it inserts the key
	 * @param key to be inserted at index
	 */
	private void insertAt(int index, Comparable<T> key) 
	{
		for (int i = numberOfKeys; i > index; --i) 
		{
			this.setKey(i, this.getKey(i - 1));
			this.setChild(i+1, this.getChild(i));
		}
		this.setKey(index, key);
		numberOfKeys++;
	}
	/**insert key and adjust left pointer with given child.
	 * @param index where key is inserted
	 * @param key to be inserted in that index
	 * @param leftChild child which this node points to with pointer at left of that index
	 */
	public void insertLeftAt(int index, Comparable<T> key, BPTreeNode<T> leftChild) 
	{
		insertAt(index, key);
		this.setChild(index+1, this.getChild(index));
		this.setChild(index, leftChild);
	}
	/**insert key and adjust right pointer with given child.
	 * @param index where key is inserted
	 * @param key to be inserted in that index
	 * @param rightChild child which this node points to with pointer at right of that index
	 */
	public void insertRightAt(int index, Comparable<T> key, BPTreeNode<T> rightChild)
	{
		insertAt(index, key);
		this.setChild(index + 1, rightChild);
	}
	/**
	 * delete key and return true or false if it is deleted or not
	 */
	public boolean delete(T key, BPTreeInnerNode<T> parent, int ptr) 
	{
		boolean done = false;
		for(int i = 0; !done && i < numberOfKeys; ++i)
			if(keys[i].compareTo(key) > 0)
				done = children[i].delete(key, this, i);
			
		if(!done)
			done = children[numberOfKeys].delete(key, this, numberOfKeys);
		if(numberOfKeys < this.minKeys())
		{
			if(isRoot())
			{
				this.getFirstChild().setRoot(true);
				this.setRoot(false);
				return done;
			}
			//1.try to borrow
			if(borrow(parent, ptr))
				return done;
			//2.merge
			merge(parent, ptr);
		}
		return done;
	}
	/**
	 * borrow from the right sibling or left sibling in case of overflow.
	 * @param parent of the current node
	 * @param ptr index of pointer in the parent node pointing to the current node 
	 * @return true or false if it can borrow form right sibling or left sibling or it can not
	 */
	public boolean borrow(BPTreeInnerNode<T> parent, int ptr)
	{
		//check left sibling
		if(ptr > 0)
		{
			BPTreeInnerNode<T> leftSibling = (BPTreeInnerNode<T>) parent.getChild(ptr-1);
			if(leftSibling.numberOfKeys > leftSibling.minKeys())
			{
				this.insertLeftAt(0, parent.getKey(ptr-1), leftSibling.getLastChild());
				parent.deleteAt(ptr-1);
				parent.insertRightAt(ptr-1, leftSibling.getLastKey(), this);
				leftSibling.deleteAt(leftSibling.numberOfKeys - 1);
				return true;
			}
		}

		//check right sibling
		if(ptr < parent.numberOfKeys)
		{
			BPTreeInnerNode<T> rightSibling = (BPTreeInnerNode<T>) parent.getChild(ptr+1);
			if(rightSibling.numberOfKeys > rightSibling.minKeys())
			{
				this.insertRightAt(this.numberOfKeys, parent.getKey(ptr), rightSibling.getFirstChild());
				parent.deleteAt(ptr);
				parent.insertRightAt(ptr, rightSibling.getFirstKey(), rightSibling);
				rightSibling.deleteAt(0, 0);
				return true;
			}
		}
		return false;
	}
	/**
	 * try to merge with left or right sibling in case of overflow
	 * @param parent of the current node 
	 * @param ptr index of pointer in the parent node pointing to the current node
	 */
	public void merge(BPTreeInnerNode<T> parent, int ptr)
	{
		if(ptr > 0)
		{
			//merge with left
			BPTreeInnerNode<T> leftSibling = (BPTreeInnerNode<T>) parent.getChild(ptr-1);
			leftSibling.merge(parent.getKey(ptr-1), this);
			parent.deleteAt(ptr-1);			
		}
		else
		{
			//merge with right
			BPTreeInnerNode<T> rightSibling = (BPTreeInnerNode<T>) parent.getChild(ptr+1);
			this.merge(parent.getKey(ptr), rightSibling);
			parent.deleteAt(ptr);
		}
	}
	
	/**
	 * merge the current node with the passed node and pulling the passed key from the parent
	 * to be inserted with the merged node
	 * @param parentKey the pulled key from the parent to be inserted in the merged node
	 * @param foreignNode the node to be merged with the current node
	 */
	public void merge(Comparable<T> parentKey, BPTreeInnerNode<T> foreignNode)
	{
		this.insertRightAt(numberOfKeys, parentKey, foreignNode.getFirstChild());
		for(int i = 0; i < foreignNode.numberOfKeys; ++i)
			this.insertRightAt(numberOfKeys, foreignNode.getKey(i), foreignNode.getChild(i+1));
	}

	/**
	 * delete the key at the specified index with the option to delete the right or left pointer
	 * @param keyIndex the index whose key will be deleted
	 * @param childPtr 0 for deleting the left pointer and 1 for deleting the right pointer
	 */
	public void deleteAt(int keyIndex, int childPtr)	//0 for left and 1 for right
	{
		for(int i = keyIndex; i < numberOfKeys - 1; ++i)
		{
			keys[i] = keys[i+1];
			children[i+childPtr] = children[i+childPtr+1];
		}
		if(childPtr == 0)
			children[numberOfKeys-1] = children[numberOfKeys];
		numberOfKeys--;
	}
	
	/**
	 * searches for the record reference of the specified key
	 */
	@Override
	public Ref search(T key) 
	{
		return children[findIndex(key)].search(key);
	}
	
	/**
	 * delete the key at the given index and deleting its right child
	 */
	public void deleteAt(int index) 
	{
		deleteAt(index, 1);	
	}

}
