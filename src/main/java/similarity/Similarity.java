package similarity;

import org.apache.mahout.math.hadoop.similarity.cooccurrence.measures.LoglikelihoodSimilarity;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

/**
 * This is an example how you can create a simple user-defined function for Neo4j.
 */
public class Similarity
{
    @UserFunction
    @Description("similarity.LLR(AB, A, B, total) - return the log-likelihood ratio of A wrt B")
    public double LLR(
            @Name("both") long AB,
            @Name("all A") long A,
            @Name("all B") long B,
            @Name("total") long total) {

        return org.apache.mahout.math.stats.LogLikelihood.logLikelihoodRatio(AB, A-AB, B-AB, total-A-B+AB);
    }

    @UserFunction
    @Description("similarity.LLSimilarity(AB, A, B, total) - return the log likelihood similarity of A and B")
    public double LLSimilarity(
            @Name("both") long AB,
            @Name("all A") long A,
            @Name("all B") long B,
            @Name("total") long total) {

        LoglikelihoodSimilarity lls = new LoglikelihoodSimilarity();

        return lls.similarity(AB, A, B, (int) total);
    }

    @UserFunction
    @Description("similarity.LLDistance(AB, A, B, total) - return the log likelihood distance between A and B")
    public double LLDistance(
            @Name("both") long AB,
            @Name("all A") long A,
            @Name("all B") long B,
            @Name("total") long total) {

        return 1.0 - LLSimilarity(AB, A, B, total);
    }

    @UserFunction
    @Description("similarity.mutualInformation(AB, A, B, total) - return the mutual information of A and B")
    public double mutualInformation(
            @Name("both") long AB,
            @Name("all A") long A,
            @Name("all B") long B,
            @Name("total") long total) {

        // LLR = 2 * N * MI
        // MI  = LLR / 2 * N

        return this.LLR(AB, A, B, total) / (2.0 * total);
    }

    @UserFunction
    @Description("similarity.NMID(AB, A, B, total) - return the normalised mutual information distance between A and B")
    public double NMID(
            @Name("both") long AB,
            @Name("all A") long A,
            @Name("all B") long B,
            @Name("total") long total) {

        // NMID = 1 - MI / H

        double normalisedJointEntropy = org.apache.mahout.math.stats.LogLikelihood.entropy(AB, A-AB, B-AB, total-A-B+AB) / total;

        return 1.0 - (this.mutualInformation(AB, A, B, total) / normalisedJointEntropy);
    }
}