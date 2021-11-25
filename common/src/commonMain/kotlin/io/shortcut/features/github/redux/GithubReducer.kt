package io.shortcut.features.github.redux

import io.shortcut.redux.AppState
import tw.geothings.rekotlin.Action

fun githubReducer(action: Action, state: AppState): GithubState {
    var newState = state.githubState
    when(action) {
        is GithubRequests.FetchIssues -> {
            newState = newState.copy(
                status = GithubState.Status.LOADING
            )
        }
        is GithubRequests.FetchIssues.Success -> {
            newState = newState.copy(
                issues = action.issues,
                status = GithubState.Status.IDLE
            )
        }
        is GithubRequests.FetchIssues.Failure -> {
            newState = newState.copy(
                status = GithubState.Status.IDLE
            )
        }

        is GithubRequests.UpdateIssue -> {
            newState = newState.copy(
                status = GithubState.Status.LOADING
            )
        }
        is GithubRequests.UpdateIssue.Success -> {
            newState = newState.copy(
                issues = newState.issues.map {
                    if(it.number == action.issue.number) action.issue else it
                },
                status = GithubState.Status.IDLE
            )
        }
        is GithubRequests.UpdateIssue.Failure -> {
            newState = newState.copy(
                status = GithubState.Status.IDLE
            )
        }
    }
    return newState
}