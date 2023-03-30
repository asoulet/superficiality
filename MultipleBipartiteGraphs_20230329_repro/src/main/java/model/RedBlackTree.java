package model;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import model.Node.Origin;

// https://www.javadevjournal.com/data-structure/red-black-tree/
// https://github.com/javadevjournal/javadevjournal/blob/master/Java/data-structure-with-java/src/main/java/com/javadevjournal/datastructure/tree/rb/

public class RedBlackTree {

	private static Logger logger = Logger.getLogger(RedBlackTree.class);

	private Node root;
    private static final boolean RED = false;
    private static final boolean BLACK = true;
    
	protected double[] weights = null;
	protected int maximum = 0;
	protected int nodeNumber = 0;
	protected int edgeNumber = 0;
	private double exponent;
	
	public RedBlackTree(double exponent) {
		this.exponent = exponent;
		weights = new double[100];
		for (int i = 0; i < weights.length; i++)
			weights[i] = Math.pow(i, exponent);
	}

    public boolean insert(int id, Origin origin) {
    	if (id > maximum)
    		maximum = id;
    	nodeNumber++;
    	edgeNumber++;
        Node n = add(id, origin);
        if (root == null) {
            root = n;
        }
        fixTree(n);
        return true;
    }

    private Node add(int id, Origin origin) {
        Node x = root;
        Node y = null;
        Node n = new Node(id, origin);
        while (x != null) {
            y = x;
            if (x.id > id) {
                x = x.left;
            } else {
                x = x.right;
            }
        }

        if (y == null) {
            y = n;
        } else if (y.id > id) {
            n.parent = y;
            y.left = n;
        } else {
            n.parent = y;
            y.right = n;
        }
        n.updateAncestors();
        return n;
    }


    private void fixTree(Node n) {
        n.color = RED;

        while (n != null && n != root && n.parent.color == RED) {
            if (parentOf(n) == leftOf(parentOf(parentOf(n)))) {
                Node y = rightOf(parentOf(parentOf(n)));
                if (colorOf(y) == RED) {
                    // we do color flip.
                    setColor(parentOf(n), BLACK);
                    setColor(parentOf(parentOf(n)), RED);
                    setColor(y, BLACK);
                    n = parentOf(parentOf(n));
                } else {
                    // we do rotate
                    if (rightOf(parentOf(n)) == n) {
                        //right child
                        n = parentOf(n);
                        leftRotate(parentOf(n));

                    }
                    setColor(parentOf(n), BLACK);
                    setColor(parentOf(parentOf(n)), RED);
                    rightRotate(parentOf(parentOf((n))));
                }
            } else {

                Node y = leftOf(parentOf(parentOf(n)));
                if (colorOf(y) == RED) {
                    // we do color flip.
                    setColor(parentOf(n), BLACK);
                    setColor(parentOf(parentOf(n)), RED);
                    setColor(y, BLACK);
                    n = parentOf(parentOf(n));
                } else {
                    // we do rotate
                    if (leftOf(parentOf(n)) == n) {
                        //right child
                        n = parentOf(n);
                        rightRotate(parentOf(n));

                    }
                    setColor(parentOf(n), BLACK);
                    setColor(parentOf(parentOf(n)), RED);
                    leftRotate(parentOf(parentOf((n))));
                }
            }
        }

        root.color = BLACK;
    }

    private boolean colorOf(Node n) {
        return n != null ? n.color : BLACK;
    }

    private Node leftOf(Node n) {
        return n != null ? n.left : null;
    }

    private Node rightOf(Node n) {
        return n != null ? n.right : null;
    }

    private Node parentOf(Node n) {
        return n != null ? n.parent : null;
    }

    private void setColor(Node n, boolean color) {
        if (n != null) {
            n.color = color;
        }
    }

    private void leftRotate(Node n) {
        if (n != null && n.right != null) {
            Node temp = n.right;
            n.right = temp.left;
            n.rightWeight = sumWeight(n.right);
            n.rightNumber = sumNumber(n.right);
            if (leftOf(temp) != null) {
                temp.left.parent = n;                
            }
            temp.parent = n.parent;
            if (parentOf(n) == null) {
                root = temp;
            } else if (leftOf(parentOf(n)) == n) {
                n.parent.left = temp;
            } else {
                n.parent.right = temp;
            }
            temp.left = n;
            temp.leftWeight = sumWeight(temp.left);
            temp.leftNumber = sumNumber(temp.left);
            n.parent = temp;
        }
    }
    
    private double sumWeight(Node n) {
    	if (n != null)
    		return n.leftWeight + n.weight + n.rightWeight;
    	else
    		return 0;
    }

    private int sumNumber(Node n) {
    	if (n != null)
    		return n.leftNumber + 1 + n.rightNumber;
    	else
    		return 0;
    }

    private void rightRotate(Node n) {
        if (n != null && n.left != null) {
            Node temp = n.left;
            n.left = temp.right;
            n.leftWeight = sumWeight(n.left);
            n.leftNumber = sumNumber(n.left);
            if (rightOf(temp) != null) {
                temp.right.parent = n;
            }
            temp.parent = n.parent;
            if (parentOf(n) == null) {
                root = temp;
            } else if (rightOf(parentOf(n)) == n) {
                n.parent.right = temp;
            } else {
                n.parent.left = temp;
            }
            temp.right = n;
            temp.rightWeight = sumWeight(temp.right);
            temp.rightNumber = sumNumber(temp.right);
            n.parent = temp;

        }
    }


    public class Node {
        Node left, right, parent;
        int id;
        boolean color;
        int count = 1;
        double weight = 0;
        double leftWeight = 0;
        double rightWeight = 0;
        int leftNumber = 0;
        int rightNumber = 0;
        Origin origin = null;

        public Node(int id, Origin origin) {
            this.id = id;
            this.weight = getWeight(count);
            this.origin = origin;
        }
        
        public void updateAncestors() {
			if (parent != null)
				parent.updateAncestors(this, weight);
		}

		private void updateAncestors(Node n, double w) {
			if (left == n) {
				leftWeight += w;
				leftNumber++;
			}
			if (right == n) {
				rightWeight += w;
				rightNumber++;
			}
			if (parent != null)
				parent.updateAncestors(this, w);
		}

		public void show() {
        	if (left != null) {
        		System.out.println("(");
        		left.show();
        		System.out.println(")");
        	}
        	System.out.println(this);
        	if (right != null) {
        		System.out.println("(");
        		right.show();
        		System.out.println(")");
        	}
        }

		@Override
		public String toString() {
			return "Node [id=" + id + ", count=" + count + ", weight=" + weight + ", lw=" + leftWeight + ", rw=" + rightWeight + ", ln=" + leftNumber + ", rn=" + rightNumber + "]";
		}
		
		public void increment(int searchedId) {
			if (searchedId == id) {
				double old = weight;
				count++;
				weight = getWeight(count);
				if (parent != null)
					parent.updateAncestors(this, weight - old);
			}
			else
				if (searchedId < id) {
					if (left != null)
						left.increment(searchedId);
					else {
						logger.warn("missing id");
					}											
				}
				else {
					if (right != null)
						right.increment(searchedId);
					else {
						logger.warn("missing id");
					}
				}
		}
        
		public Node search(int searchedId) {
			if (searchedId == id) {
				return this;
			}
			else
				if (searchedId < id) {
					if (left != null)
						return left.search(searchedId);
				}
				else {
					if (right != null)
						return right.search(searchedId);
				}
			return null;
		}
		
		public int getDepth() {
			return 1 + Math.max((left != null ? left.getDepth() : 0), (right != null ? right.getDepth() : 0));
		}
		
		public double drawEdge(double value) {
			if (value < leftWeight) {
				if (left != null) {
					double w = left.drawEdge(value);
					leftWeight += w;
					return w;
				}
				else {
					logger.warn("edge draw issue: no left tree");
					return 0;
				}
			}
			value -= leftWeight;
			if (value < weight) {
				double old = weight;
				count++;
				weight = getWeight(count);
				return weight - old;
			}
			value -= weight;
			if (value < rightWeight) {
				if (right != null) {
					double w = right.drawEdge(value);
					rightWeight += w;
					return w;
				}
				else {
					logger.warn("edge draw issue: no right tree");
					return 0;
				}
			}
			logger.warn("edge draw issue: too large value");
			return 0;
		}
        
		public int drawOtherNode(double value, int min, int max) {
//			System.out.println("$ " + value + " " + min + " " + max);
			int	leftEmpty = id - min - leftNumber;
			int	rightEmpty = max - id - rightNumber;
//			System.out.println("node " + id + " " + leftEmpty + " " + rightEmpty);
			
			if (value < leftEmpty) {
				if (left != null) {
					return left.drawOtherNode(value, min, id - 1);
				}
				else {
					//int other = (int)(Math.random() * (id - min) + min);
					int other = (int) value + min;
//					System.out.println("other left " + other);
					return other;
				}
			}
			value -= leftEmpty;
			if (value < rightEmpty) {
				if (right != null) {
					return right.drawOtherNode(value, id + 1, max);
				}
				else {
					//int other = (int)(Math.random() * (max - id) + id);
					int other = (int) value + id + 1;
	//				System.out.println("other right " + other);
					return other;
				}
			}
			logger.warn("other draw issue: too large value");
			return 0;
		}
		
		public void apply(RBTConsumer consumer) {
			if (left != null)
				left.apply(consumer);
			consumer.consume(this);
			if (right != null)
				right.apply(consumer);
		}

		public void getSample(ArrayList<Integer> sample) {
			if (left != null)
				left.getSample(sample);
			sample.add(count);
			if (right != null)
				right.getSample(sample);
		}

    }
 // Node end
    
    public void apply(RBTConsumer consumer) {
    	if (root != null)
    		root.apply(consumer);
    }
    
    public void drawEdge() {
    	if (root != null) {
    		edgeNumber++;
    		root.drawEdge(Math.random() * sumWeight(root));
    	}
    }
    
    public void drawOtherNode(int range) {
    	if (root != null) {
    		int other = -1;
        	int m = Math.max(range, maximum) + 1 - sumNumber(root);
//        	System.out.println(m + " " + Math.max(range, maximum) + " " + sumNumber(root));
        	if (m == 0)
        		other = maximum + 1;
        	else
        		other = root.drawOtherNode(Math.random() * m, 0, Math.max(range, maximum));
    		if (other >= 0) {
    			insert(other, Origin.OTHER);
    		}
    		else
    			logger.warn("other draw issue with -1 as id");
    	}
    	else {
    		int other = (int) (Math.random() * range);
//    		System.out.println(other);
    		insert(other, Origin.OTHER);
    	}
    }

    public int getDepth() {
    	if (root != null)
    		return root.getDepth();
    	else
    		return 0;
    }
    
    public void show() {
    	System.out.println("-------------------------------------");
    	if (root != null)
    		root.show();
    }
    
	private final double getWeight(Integer counter) {
		if (counter < weights.length)
			return weights[counter];
		return Math.pow(counter, exponent);
	}

	public void increment(int searchedId) {
		if (root != null)
			root.increment(searchedId);
	}

	public Node search(int searchedId) {
		if (root != null)
			return root.search(searchedId);
		return null;
	}
	
	public int getMaximum() {
		return maximum;
	}
	
	public int getNodeNumber() {
		return nodeNumber;
	}
	
	public int getEdgeNumber() {
		return edgeNumber;
	}
	
	

	public static void main(String[] args) {
		int n = 10;
        RedBlackTree redBlackTree = new RedBlackTree(1);
        for (int i = 0; i <= n; i++) {
        redBlackTree.drawOtherNode(n);
        }
        /*redBlackTree.insert(15);
        redBlackTree.insert(6);
        redBlackTree.insert(5);
        redBlackTree.insert(25);
        redBlackTree.insert(27);
        redBlackTree.insert(7);
        redBlackTree.insert(19);*/
/*        for (int i = 0; i < 100; i++) {
        	int j = 0;
        	do {
        		j = (int) (Math.random() * 1000);
        	} while (redBlackTree.search(j) != null);
        	redBlackTree.insert(j);
        }*/
        /*for (int i = 0; i < 10; i++)
        	redBlackTree.draw();*/
        redBlackTree.show();
//        System.out.println(redBlackTree.getDepth());
    }

	public ArrayList<Integer> getSample() {
		ArrayList<Integer> sample = new ArrayList<Integer>();
		if (root != null)
			root.getSample(sample);
		return sample;
	}


}