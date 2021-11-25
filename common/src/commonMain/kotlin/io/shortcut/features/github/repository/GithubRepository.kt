package io.shortcut.features.github.repository

import io.ktor.client.*
import io.ktor.client.request.*
import io.shortcut.features.github.entity.Issue
import io.shortcut.network.HttpClientFactory
import io.shortcut.network.request

internal class GithubRepository(
    private val _httpClient: HttpClient = HttpClientFactory().create()
) {
    suspend fun getIssues(user: String, repo: String) = request {
        _httpClient.get<List<Issue>>(path = "repos/$user/$repo/issues")
    }

    suspend fun updateIssue(user: String, repo: String, issue: Issue) = request {
        _httpClient.patch<Issue>(
            path = "repos/$user/$repo/issues/${issue.number}",
            body = mapOf(
                "title" to issue.title,
                "body" to issue.body
            )
        ) {
            header("Content-Type", "application/json")
            header("Authorization", "Bearer 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")
        }
    }
}