package octopus.teamcity.agent;

import jetbrains.buildServer.agent.BuildProgressLogger;
import jetbrains.buildServer.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class OctopusMetadataBuilder {

    private BuildProgressLogger buildLogger;

    public OctopusMetadataBuilder(final BuildProgressLogger buildLogger){

        this.buildLogger = buildLogger;
    }

    public OctopusPackageMetadata build(
            final String vcsRoot,
            final String vcsCommitNumber,
            final String comments,
            final String commentParser,
            final String serverUrl,
            final String buildId,
            final String buildNumber) throws Exception {

        final OctopusPackageMetadata metadata = new OctopusPackageMetadata();

        if (!StringUtil.isEmptyOrSpaces(commentParser)) {
            final CommentParserFactory parserFactory = new CommentParserFactory();
            final CommentParser parser = parserFactory.getParser(commentParser);

            metadata.IssueTrackerId = "issuetracker-" + parser.getIssueTrackerSuffix();

            final List<WorkItem> workItems = new ArrayList<WorkItem>();

            if (comments != null && !comments.isEmpty()) {
                final List<WorkItem> items = parser.parse(comments, buildLogger);
                workItems.addAll(items);
            }

            if (workItems.size() > 0) {
                buildLogger.message("Found work items in comments, adding " + workItems.size() + " work items to octopus.metadata");

                metadata.WorkItems = workItems;

            } else {
                buildLogger.message("No work items found in comments");
            }
        }

        metadata.BuildNumber = buildNumber;
        metadata.BuildLink = serverUrl + "/viewLog.html?buildId=" + buildId;
        metadata.VcsRoot = vcsRoot;
        metadata.VcsCommitNumber = vcsCommitNumber;

        return metadata;
    }
}
