package io.shortcut.android.features

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import io.shortcut.features.github.entity.Issue
import io.shortcut.features.github.redux.GithubRequests
import io.shortcut.features.github.redux.GithubState
import io.shortcut.redux.store
import tw.geothings.rekotlin.StoreSubscriber

class MainActivity : ComponentActivity(), StoreSubscriber<GithubState> {

    private val githubState = MutableLiveData<GithubState>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainView()
        }

        store.dispatch(GithubRequests.FetchIssues("mortennost", "kmp-mobile"))
    }

    override fun onNewState(state: GithubState) {
        githubState.postValue(state)
    }

    override fun onStart() {
        super.onStart()

        store.subscribe(this) { state ->
            state.skipRepeats { oldState, newState ->
                oldState.githubState == newState.githubState
            }.select { it.githubState }
        }
    }

    override fun onStop() {
        super.onStop()
        store.unsubscribe(this)
    }

    @Composable
    private fun MainView(
        modifier: Modifier = Modifier
    ) {
        val state = githubState.observeAsState().value ?: return

        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            when(state.status) {
                GithubState.Status.IDLE -> IssuesList(issues = state.issues)
                else -> CircularProgressIndicator()
            }
        }
    }

    @Composable
    private fun IssuesList(issues: List<Issue>) =
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(issues) { issue ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                        .clickable {
                            showIssueDetails(issue.number)
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = "#${issue.number}: ${issue.title}",
                            style = MaterialTheme.typography.h6
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = issue.body,
                            style = MaterialTheme.typography.body1
                        )
                    }
                }
            }
        }

    private fun showIssueDetails(issueNumber: Int) =
        startActivity(DetailsActivity.createIntent(this, issueNumber))
}
