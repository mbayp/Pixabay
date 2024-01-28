package com.example.pixabay

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pixabay.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var page = 1
    var adapter = PixaAdapter(arrayListOf())
    var oldWord = ""
    var newWord = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initClickers()
        setupRecyclerViewScrollListener()
    }

    private fun initClickers() {
        with(binding) {
            nextBtn.setOnClickListener() {
                newWord = searchEd.text.toString()
                if (newWord != oldWord){
                    Toast.makeText(this@MainActivity, "Чтоб найти новое нажмите 'search'", Toast.LENGTH_SHORT).show()
                } else {
                    page++
                    getImage()
                }
            }
            searchBtn.setOnClickListener() {
                adapter.list.clear()
                page = 1
                getImage()
            }
        }
    }

    private fun setupRecyclerViewScrollListener() {
        binding.pixaRecycler.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {

                        loadMoreData()
                    }
                }
            })
        }
    }

    private fun loadMoreData() {
        oldWord = binding.searchEd.text.toString()
        RetrofitService().api.getImages(keyWordForSearch = oldWord, page = page)
            .enqueue(object : Callback<PixabayModel> {
                override fun onResponse(call: Call<PixabayModel>, response: Response<PixabayModel>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            adapter.list.addAll(it.hits)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }

                override fun onFailure(call: Call<PixabayModel>, t: Throwable) {
                    Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun getImage() {
        oldWord = binding.searchEd.text.toString()
        RetrofitService().api.getImages(keyWordForSearch = oldWord, page = page)
            .enqueue(object : Callback<PixabayModel> {
                override fun onResponse(call: Call<PixabayModel>, response: Response<PixabayModel>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            adapter.list.addAll(it.hits)
                            binding.pixaRecycler.adapter = adapter
                        }
                    }
                }

                override fun onFailure(call: Call<PixabayModel>, t: Throwable) {
                    Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }
}