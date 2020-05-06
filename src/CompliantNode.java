import java.util.*;

/* CompliantNode refers to a node that follows the rules (not malicious)*/
public class CompliantNode implements Node {

    private final double p_graph;
    private final double p_malicious;
    private final double p_txDistribution;
    private final int numRounds;
    private int currRound;
    private Set<Integer> followees; // ??
    private Set<Transaction> proposedTransactions;
    private Map<Transaction, Integer> consensusCount;

    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        this.p_graph = p_graph;
        this.p_malicious = p_malicious;
        this.p_txDistribution = p_txDistribution;
        this.numRounds = numRounds;
        currRound = 0;

        this.followees = new HashSet<>();
        this.proposedTransactions = new HashSet<>();
        this.consensusCount = new HashMap<>();
    }

    // initialize: nodes that this node follows
    public void setFollowees(boolean[] followees) {
        for (int i = 0; i < followees.length; i++)
            if (followees[i]) this.followees.add(i);
    }

    // initialize: the transactions randomly sent from the network
    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        for (Transaction tx : pendingTransactions) {
            if (!this.proposedTransactions.contains(tx)) this.proposedTransactions.add(tx);
            consensusCount.put(tx, consensusCount.getOrDefault(tx, 0) + 1);
        }
    }

    // send proposals to followers.
    public Set<Transaction> sendToFollowers() {
        if (++currRound == numRounds) {
            // new behaviour: return transactions reached upon consensus
            Queue<Transaction> consensus = new PriorityQueue<>(
                    (a, b) -> consensusCount.get(b) - consensusCount.get(a)
            );

            // sort by transaction count
            for (Transaction tx : consensusCount.keySet())
                consensus.add(tx);


            int numNodes = consensusCount.size();
            int numTx = (int) (numNodes * (1 - p_malicious) );
            proposedTransactions = new HashSet<>();
            for (int i = 0; i < numTx; i++)
                proposedTransactions.add(consensus.poll());
        }
        return proposedTransactions;
    }

    // Receive candidates from other nodes
    public void receiveFromFollowees(Set<Candidate> candidates) {
        for (Candidate c : candidates) {
            Transaction tx = c.tx;
//            int sender = c.sender;
            consensusCount.put(tx, consensusCount.getOrDefault(tx, 0) + 1);
        }
    }
}
