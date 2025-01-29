package com.plcoding.focusfun.landing

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.plcoding.oraclewms.api.ApiResponse

class LandingViewModel : ViewModel() {

    private val TAG = LandingViewModel::class.java.simpleName
    private var response : ApiResponse? = null
    fun setResponse(res : ApiResponse?){
        response = res
    }

    fun getResponse() : ApiResponse?{
        return response
    }

//    var marketUiState: MarketUiState by mutableStateOf(MarketUiState.Empty)
//        private set
//
//    fun fetchNftWithType(type: String) {
//        marketUiState = MarketUiState.Loading
//        val obj = JsonObject()
//        obj.addProperty("type_of", type)
//        BaseApiInterface.create()
//            .getNftWithType(
//                BuildConfig.GET_NFT_WITH_TYPE,
//                obj,
//                "Bearer ${SharedPref.getToken()}"
//            ).enqueue(object : Callback<List<NFTResponse>> {
//                override fun onResponse(
//                    call: Call<List<NFTResponse>>,
//                    response: Response<List<NFTResponse>>
//                ) {
//                    if (response.isSuccessful) {
//                        response.body().let {
//                            if (it == null) marketUiState = MarketUiState.Error
//                            else marketUiState = MarketUiState.Success(it)
//                        }
//                    } else marketUiState = MarketUiState.Error
//                }
//
//                override fun onFailure(call: Call<List<NFTResponse>>, t: Throwable) {
//                    marketUiState = MarketUiState.Error
//                }
//            })
//    }

    var _buyNftState: Int by mutableIntStateOf(3) ///0->load 1->success 2->fail 3->empty
        private set
}