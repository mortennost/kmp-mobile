package io.shortcut.android.features

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import io.shortcut.features.github.entity.Issue
import io.shortcut.features.github.redux.GithubRequests
import io.shortcut.features.github.redux.GithubState
import io.shortcut.redux.store
import tw.geothings.rekotlin.StoreSubscriber

class DetailsActivity : ComponentActivity(), StoreSubscriber<GithubState> {
    private val _issueState = MutableLiveData<Issue?>()

    override fun onNewState(state: GithubState) {
        val issueNumber = intent.getIntExtra(KEY_ISSUE_NUMBER, -1)
        val issue = when(state.status) {
            GithubState.Status.IDLE -> state.issues.firstOrNull { it.number == issueNumber }
            GithubState.Status.LOADING -> null
        }
        _issueState.postValue(issue)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DetailsView()
        }
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

    private fun updateIssue(issueNumber: Int, title: String, body: String) =
        store.dispatch(GithubRequests.UpdateIssue(
            "mortennost",
            "kmp-mobile",
            Issue(
                issueNumber,
                title,
                body
            )
        ))

    @Composable
    private fun DetailsView(
        modifier: Modifier = Modifier
    ) {
        val issue = _issueState.observeAsState().value ?: return
        var title by remember { mutableStateOf(issue.title) }
        var body by remember { mutableStateOf(issue.body) }

        Column (
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = title,
                onValueChange = { title = it }
            )
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = body,
                onValueChange = { body = it }
            )
            Button(onClick = {
                updateIssue(
                    issue.number,
                    title,
                    body
                )
            }) {
                Text(text = "Save")
            }
        }
    }

    companion object {
        private const val KEY_ISSUE_NUMBER = "issue_number"

        fun createIntent(
            context: Context,
            issueNumber: Int
        ) = Intent(context, DetailsActivity::class.java).apply {
            putExtra(KEY_ISSUE_NUMBER, issueNumber)
        }
    }
}