package com.prography.home.ui.search.screen

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.prography.domain.model.TagModel
import com.prography.domain.model.TagWithCount
import com.prography.home.ui.search.contract.SearchAction
import com.prography.home.ui.search.contract.SearchEffect
import com.prography.home.ui.search.contract.SearchStage
import com.prography.home.ui.search.contract.SearchState
import com.prography.home.ui.search.viewmodel.SearchViewModel
import com.prography.home.ui.search.viewmodel.SearchResultsViewModel
import com.prography.ui.theme.PrographyTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onNavigateToStorage: () -> Unit = {},
    searchViewModel: SearchViewModel = hiltViewModel(),
    searchResultsViewModel: SearchResultsViewModel = hiltViewModel()
) {
    val searchState by searchViewModel.uiState.collectAsState()
    val resultsState by searchResultsViewModel.uiState.collectAsState()

    val stage by remember(searchState, resultsState) {
        mutableStateOf(
            when {
                // 검색어 있고 자동완성 ON & 결과 리스트 존재할 때 중간단
                searchState.showAutocomplete && searchState.searchQuery.isNotBlank() -> SearchStage.AUTOCOMPLETE

                // 선택 태그가 하나 이상이면 결과 화면 (hasSearched는 VM에서 관리)
                resultsState.selectedTags.isNotEmpty() -> SearchStage.RESULTS

                else -> SearchStage.HOME
            }
        )
    }

    // 2) 최초 진입 시 인기 태그 로딩 (한 번)
    LaunchedEffect(Unit) { searchViewModel.loadMostUsedTags() }

    // 3) stage가 RESULTS로 바뀌면, 그때만 결과 로드
    LaunchedEffect(stage, resultsState.selectedTags) {
        if (stage == SearchStage.RESULTS && resultsState.selectedTags.isNotEmpty()) {
            searchResultsViewModel.loadSearchResults(resultsState.selectedTags)
        }
    }

    // 4) 공통 핸들러: 홈으로 복귀
    fun goHome() {
        // 검색 쿼리/자동완성/선택태그/결과 초기화
        searchViewModel.sendAction(SearchAction.ClearSearch)         // 쿼리/자동완성 플래그 초기화 용
        searchResultsViewModel.sendAction(SearchAction.ClearSearch)  // 선택태그/결과 초기화 용
    }

    // 5) 화면 전환 없이 “상태 기반” UI
    when (stage) {
        SearchStage.HOME -> {
            SearchContent(
                state = searchState,
                onAction = { action ->
                    when (action) {
                        // 입력 완료 → VM이 showAutocomplete=false로 두고 선택 태그/검색 진행하도록
                        SearchAction.OnSearchComplete -> {
                            searchViewModel.sendAction(action)
                            // 여기서 태그가 추가되는 순간 resultsState.selectedTags가 갱신 → stage가 RESULTS로 전환됨
                        }

                        // 자동완성에서 태그 탭 → 태그 추가
                        is SearchAction.AddTag -> {
                            searchViewModel.sendAction(action)
                            searchResultsViewModel.sendAction(SearchAction.AddTag(action.tag))
                        }

                        // 기타(SearchBar 입력 등)
                        else -> searchViewModel.sendAction(action)
                    }
                },
                modifier = modifier
            )
        }

        SearchStage.AUTOCOMPLETE -> {
            // SearchContent 자체가 자동완성 리스트/빈 상태를 모두 처리
            SearchContent(
                state = searchState,
                onAction = { action ->
                    when (action) {
                        // 자동완성 탭 → 태그 추가 → 결과 화면으로 (상태로 전환)
                        is SearchAction.AddTag -> {
                            searchViewModel.sendAction(action)
                            searchResultsViewModel.sendAction(SearchAction.AddTag(action.tag))
                        }

                        // 취소/뒤로 → 홈
                        SearchAction.NavigateToStorage -> onNavigateToStorage()
                        SearchAction.ClearSearch -> goHome()

                        else -> searchViewModel.sendAction(action)
                    }
                },
                modifier = modifier
            )
        }

        SearchStage.RESULTS -> {
            SearchResultsContent(
                state = resultsState,
                onAction = { action ->
                    when (action) {
                        // 결과 헤더의 뒤로가기 → 홈
                        SearchAction.NavigateBackToSearch -> goHome()

                        // 결과 헤더의 전체 지우기 → 홈
                        SearchAction.ClearSearch -> goHome()

                        // 태그 개별 제거: 전부 없어지면 stage가 자동으로 HOME으로
                        is SearchAction.RemoveTag -> {
                            searchResultsViewModel.sendAction(action)
                            // VM에서 selectedTags가 비면 stage=HOME으로 자연 전환
                        }

                        // 연관 태그 추가: 결과 화면 내에서 태그 AND 추가
                        is SearchAction.AddTag -> {
                            // 선택태그는 results VM이 단일 소스로 관리
                            searchResultsViewModel.sendAction(action)
                        }

                        // 결과 아이템 클릭 등
                        else -> searchResultsViewModel.sendAction(action)
                    }
                },
                modifier = modifier
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SearchScreenWithTagsPreview() {
    val sampleState = SearchState(
        popularTags = listOf(
            TagWithCount(0, "쇼핑", 25),
            TagWithCount(0, "여행", 18),
            TagWithCount(0, "음식", 15),
            TagWithCount(0, "강아지", 12),
            TagWithCount(0, "세상에서 제일 귀여운 강아지들", 8),
            TagWithCount(0, "통키", 6),
            TagWithCount(0, "여러분", 4)
        ),
        isLoading = false
    )

    PrographyTheme {
        SearchContent(
            state = sampleState,
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenEmptyPreview() {
    PrographyTheme {
        SearchContent(
            state = SearchState(
                isLoading = false
            ),
            onAction = {}
        )
    }
}