package BPTree;

import java.util.Scanner;

public class TestBPTree {

	public static void main(String[] args) 
	{
		BPTree<Integer> tree = new BPTree<Integer>(4);
		Scanner sc = new Scanner(System.in);
		while(true) 
		{
			int x = sc.nextInt();
			if(x == -1)
				break;
			tree.insert(x, null);
			System.out.println(tree.toString());
		}
		while(true) 
		{
			int x = sc.nextInt();
			if(x == -1)
				break;
			tree.delete(x);
			System.out.println(tree.toString());
		}
		sc.close();
	}	
}
