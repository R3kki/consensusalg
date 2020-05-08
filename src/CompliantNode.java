import java.util.*;

/* CompliantNode refers to a node that follows the rules (not malicious)*/
public class CompliantNode implements Node {

    private final double p_graph;           // % of connecting edges
    private final double p_malicious;       // % of malicious nodes
    private final double p_txDistribution;  // % of nodes having an initial set of transactions
    private final int numRounds;

    private int numFollowees;
    private int currRound;
    private Map<Transaction, Set<Integer>> candidateTransactions;

    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        this.p_graph = p_graph;
        this.p_malicious = p_malicious;
        this.p_txDistribution = p_txDistribution;
        this.numRounds = numRounds;
        currRound = 0;
        candidateTransactions = new HashMap<>();
    }

    // initialize: nodes that this node follows (following nodes)
    public void setFollowees(boolean[] followees) {
        for (int i = 0; i < followees.length; i++)
            if (followees[i]) numFollowees++;
    }

    // initialize: the transactions randomly sent from the network according to prob dist p_txDistribution
    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        for (Transaction tx : pendingTransactions)
            candidateTransactions.put(tx, new HashSet<>());
    }

    // send proposals to followers. And filter out for bad ones
    public Set<Transaction> sendToFollowers() {
        if (++currRound == numRounds) {
            // new behaviour: return transactions reached upon consensus
            int numCompliantNodes = (int) (numFollowees * (1 - p_malicious));
            for (Transaction tx : candidateTransactions.keySet()){
                if (candidateTransactions.get(tx).size() < numCompliantNodes)
                    candidateTransactions.remove(tx);
            }
        }
        return candidateTransactions.keySet();
    }

    // Receive candidates by listening to nodes that this node follows (following/followee)
    // candidate sender is the followee
    public void receiveFromFollowees(Set<Candidate> candidates) {
        for (Candidate c : candidates){
            Integer followee = c.sender;
            Transaction tx = c.tx;
            if (!candidateTransactions.containsKey(tx)) candidateTransactions.put(tx, new HashSet<>());
            candidateTransactions.get(tx).add(followee);
        }

    }
}
