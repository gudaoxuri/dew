package group.idealworld.dew.core.cluster.test;

import group.idealworld.dew.core.cluster.ClusterElection;

/**
 * Cluster election test.
 *
 * @author gudaoxuri
 */
public class ClusterElectionTest {

    /**
     * Test.
     *
     * @param election the election
     * @throws InterruptedException the interrupted exception
     */
    public void test(ClusterElection election) throws InterruptedException {
        Thread.sleep(10000);
        assert election.isLeader();
    }

}
