package io.github.jfcameron.githubget;

import static io.github.jfcameron.githubget.Application.token;
import io.github.jfcameron.githubget.taf.Command;
import io.github.jfcameron.githubget.taf.Parameter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author josephcameron
 */
public class AccountCommand extends Command
{
    public AccountCommand()
    {
        super("account", ""
                + "get info about a github account specified by the first positional\n"
                + "eg: account [account]\n"
                + "zero options: print basic account info\n"
                + "-r: no basic info, list account repos instead\n"
                + "-f: ignore forks in repo list");
    }

    private enum Mode
    {
        BasicInfo,
        Repositories,
        Followers,
        Following
    }

    @Override
    protected void usermain(Parameter.List aParameters)
    {
        Mode mode = Mode.BasicInfo;

        if (aParameters.containsLongOption("repos"))
            mode = Mode.Repositories;
        else
            if (aParameters.containsLongOption("followers"))
                mode = Mode.Followers;
            else
                if (aParameters.containsLongOption("following"))
                    mode = Mode.Following;

        String accountName = aParameters.getPositional(0);

        if (accountName != null)
            try
            {
                final Account account = new Account(token, accountName, aParameters.containsOption('f'));

                switch (mode)
                {
                    case Repositories:
                    {
                        account.getRepositories().forEach((repo) ->
                        {
                            System.out.println(repo.getName());
                        });
                    }
                    break;

                    case Followers:
                    {
                        account.getFollowers().forEach((follower) ->
                        {
                            System.out.println(follower.getLogin());
                        });
                    }
                    break;

                    case Following:
                    {
                        account.getFollowing().forEach((following) ->
                        {
                            System.out.println(following.getLogin());
                        });
                    }
                    break;

                    default:
                    case BasicInfo:
                    {
                        System.out.println(account.getName());
                        System.out.println(account.getCompany());
                        System.out.println(account.getBlog());
                        System.out.println(account.getLocation());
                        System.out.println(account.getEmail());
                        System.out.println(account.getBiography());
                        System.out.println(account.getAvatarURL());
                        System.out.println("Public repos #: " + account.getPublicRepoCount());
                        System.out.println("Public gists #: " + account.getPublicGistCount());
                        System.out.println("Followers #: " + account.getFollowerCount());
                        System.out.println("Following #: " + account.getFollowingCount());
                    }
                    break;
                }
            }
            catch (Exception ex)
            {
                Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
}
