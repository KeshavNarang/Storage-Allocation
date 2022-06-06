import java.io.*;
import java.util.*;
public class StorageManager
{
	public static void main (String [] args)
	{
		int numberStorageUnits = 200;	
		
		int startIndex = 0;
		int randomNumberBlocks = 0;
		int randomList = 0;
		
		// Currently linked lists
		
		LinkedList <Request> allocations = new LinkedList <Request> ();
		LinkedList <Request> free = new LinkedList <Request> ();
		
		// Fill both lists with contiguous segments randomly until all 200 blocks have been assigned		
		while (startIndex < numberStorageUnits)
		{
			// How many blocks (1 - 10) to go in the list
			randomNumberBlocks = (int) (Math.random() * 10) + 1;
			
			// Make sure that no more than 200 blocks have been assigned (not 201, 203, 209, etc)
			if ((startIndex + randomNumberBlocks) > numberStorageUnits)
			{
				while ((startIndex + randomNumberBlocks) > numberStorageUnits)
				{
					randomNumberBlocks = (int) (Math.random() * 10) + 1;
				}
			}
			
			// Assign the contiguous blocks to a random list
			randomList = (int) (Math.random() * 2) + 1;			
			
			// Add as a new person's order to the allocation list
			if (randomList == 1)
			{
				allocations.add(new Request (startIndex, randomNumberBlocks));
			}
			
			// Add as free blocks in the free list. Merge continguous free blocks.
			else if (randomList == 2)
			{
				if (free.size() == 0)
				{
					free.add(new Request (startIndex, randomNumberBlocks));
				}
				else
				{
					Request lastRequest = free.getLast();
					if ((lastRequest.getStartIndex() + lastRequest.getNumberContiguousBlocks()) == startIndex)
					{
						lastRequest.increaseNumberBlocks(randomNumberBlocks);
					}
					else
					{
						free.add(new Request (startIndex, randomNumberBlocks));
					}
				}
			}
			
			startIndex = startIndex + randomNumberBlocks;
		}
		System.out.println("Original Data: ");
		printData(allocations, free);
		
		// Initial random allocations met
		
		// Make 10 requests
		for (int i = 1; i <= 10; i++)
		{
			int randomRequest = (int) (Math.random() * 2) + 1;
			if (allocations.size() == 0)
			{
				randomRequest = 1;
			}
			
			// Randomly choose between an adding or deleting request
			
			System.out.println("-----------------------------------------------------------------------------------");
			System.out.println();
			System.out.print("Request " + i + ": ");
			if (randomRequest == 1)
			{
				addRequest(allocations, free);
			}
			else if (randomRequest == 2)
			{
				deleteRequest(allocations, free);
			}
		}
	}
	
	
	public static void addRequest (LinkedList <Request> allocations, LinkedList <Request> free)
	{
		// Randomly generate a number of blocks to generate
		int amountToAdd = (int) (Math.random() * 10) + 1;
		System.out.print("Add " + amountToAdd + " units. ");
		
		// Find the optimal spot to remove from the free list
		Request bestFitRequest = null; 
		
		// For each contiguous section, see if it has at least as many blocks as needed, and as few more as possible
		for (Request request : free)
		{
			if (request.getNumberContiguousBlocks() >= amountToAdd)
			{
				if (bestFitRequest == null)
				{
					bestFitRequest = request;
				}
				else if (request.getNumberContiguousBlocks() < bestFitRequest.getNumberContiguousBlocks())
				{
					bestFitRequest = request;
				}
			}
		}
		
		if (bestFitRequest == null)
		{
			System.out.println("Sorry! Not enough (contiguous) storage."); 
		}
		
		// Remove the best-fit section from the free list and add it to the allocation list
		else
		{
			System.out.println("Now stored at index " + bestFitRequest.getStartIndex() + ".");
			allocations.add(new Request (bestFitRequest.getStartIndex(), amountToAdd));
			
			if (bestFitRequest.getNumberContiguousBlocks() == amountToAdd)
			{
				free.remove(bestFitRequest);	
			}
			
			else
			{
				int newStartIndex = bestFitRequest.getStartIndex() + amountToAdd;
				int newContiguousBlock = bestFitRequest.getNumberContiguousBlocks() - amountToAdd;
				
				free.remove(bestFitRequest);
				free.add(new Request (newStartIndex, newContiguousBlock));
			}
		}
		printData(allocations, free);
	}
	
	public static void deleteRequest (LinkedList <Request> allocations, LinkedList <Request> free)
	{	
		// Pick a random contiguous section to delete
		int requestToClear = (int) (Math.random() * allocations.size());
		
		int counter = 0;
		Request delete = null;
		
		for (Request request : allocations)
		{
			if (counter == requestToClear)
			{
				delete = request;
			}
			counter++;
		}
		
		System.out.println("Clear " + delete.getNumberContiguousBlocks() + " blocks from index " + delete.getStartIndex());
		
		boolean found = false;
		Iterator <Request> iterator = free.iterator();
		int trueStartIndex = delete.getStartIndex();
		int trueNumberContiguousBlocks = delete.getNumberContiguousBlocks();
		
		// Remove the appropriate section from the allocation list and add it to the free list. 
		// Then merge it with any other free blocks next to it.
		while (iterator.hasNext()) 
		{
			Request request = iterator.next();
			if (request.getStartIndex() + request.getNumberContiguousBlocks() == delete.getStartIndex())
			{
				trueStartIndex = request.getStartIndex();
				trueNumberContiguousBlocks = request.getNumberContiguousBlocks() + delete.getNumberContiguousBlocks();
				iterator.remove();
			}
			if (delete.getStartIndex() + delete.getNumberContiguousBlocks() == request.getStartIndex())
			{
				trueStartIndex = delete.getStartIndex();
				trueNumberContiguousBlocks = delete.getNumberContiguousBlocks() + request.getNumberContiguousBlocks();
				iterator.remove();
			}
		}

		
		free.add(new Request(trueStartIndex, trueNumberContiguousBlocks));
		allocations.remove(delete);
		
		printData(allocations, free);
	}
	
	public static void printData (LinkedList <Request> allocations, LinkedList <Request> free)
	{
		System.out.println();
		System.out.println("Allocation List: ");
		System.out.println(allocations);
		System.out.println();
		
		System.out.println("Free List: ");
		System.out.println(free);
		System.out.println();
	}
}

class Request
{
	private int startIndex;
	private int numberContiguousBlocks;
	
	public Request (int startIndex, int numberContiguousBlocks)
	{
		this.startIndex = startIndex;
		this.numberContiguousBlocks = numberContiguousBlocks;
	}
	
	public int getStartIndex ()
	{
		return startIndex;
	}
	
	public int getNumberContiguousBlocks ()
	{
		return numberContiguousBlocks;
	}
	
	public void increaseNumberBlocks (int numberBlocks)
	{
		numberContiguousBlocks += numberBlocks;
	}
	
	public String toString ()
	{
		return "(" + startIndex + ", " + numberContiguousBlocks + ")";
	}
}