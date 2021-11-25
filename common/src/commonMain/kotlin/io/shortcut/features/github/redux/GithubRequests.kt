package io.shortcut.features.github.redux

import io.shortcut.features.github.entity.Issue
import io.shortcut.features.github.repository.GithubRepository
import io.shortcut.network.Response
import io.shortcut.redux.Request
import io.shortcut.redux.ToastAction
import io.shortcut.redux.store
import io.shortcut.util.Strings
import tw.geothings.rekotlin.Action

class GithubRequests {
    class FetchIssues(val user: String, val repo: String): Request() {
        private val _githubRepository = GithubRepository()

        override suspend fun execute() {
            val result = when(val response = _githubRepository.getIssues(user, repo)) {
                is Response.Success -> Success(response.result)
                is Response.Failure -> Failure(response.error)
            }
            store.dispatch(result)
        }

        data class Success(val issues: List<Issue>): Action
        data class Failure(override val message: String?): ToastAction
    }

    class UpdateIssue(
        val user: String,
        val repo: String,
        val issue: Issue
    ): Request() {
        private val _githubRepository = GithubRepository()

        override suspend fun execute() {
            val result = when(val response = _githubRepository.updateIssue(user, repo, issue)) {
                is Response.Success -> Success(response.result)
                is Response.Failure -> Failure(Strings.get("internal_error"))
            }
            store.dispatch(result)
        }

        data class Success(val issue: Issue): Action
        data class Failure(override val message: String?): ToastAction
    }
}