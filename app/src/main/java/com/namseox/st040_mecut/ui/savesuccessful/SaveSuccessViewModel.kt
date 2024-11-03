package com.namseox.st040_mecut.ui.savesuccessful

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.namseox.st040_mecut.data.model.VideoModel
import com.namseox.st040_mecut.data.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaveSuccessViewModel  @Inject constructor(app: Application, var providerRepository: VideoRepository) :
    AndroidViewModel(app) {
    var listArrVideo = MutableLiveData<ArrayList<VideoModel>>()
    var x = MutableLiveData<Int>()
    fun getAllVideo() {
        viewModelScope.launch(Dispatchers.IO) {
            listArrVideo.postValue(providerRepository.getAllVideo() as ArrayList<VideoModel>?)
        }
    }
    fun deleteVideo(video : VideoModel){
        viewModelScope.launch(Dispatchers.IO) {
            x.postValue(providerRepository.deleteVideo(video))
        }
    }
    fun deleteAll(){
        viewModelScope.launch(Dispatchers.IO) {
            x.postValue(providerRepository.deleteAll())
        }
    }
    fun addVideo(video : VideoModel){
        viewModelScope.launch(Dispatchers.IO) {
            x.postValue(providerRepository.addVideo(video))
        }
    }
}