package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class MainActivity2 : AppCompatActivity() {
    val serviceKey="O17EX7gdN3IhRhEPUn7x3%2Fndd0%2Boq4smJrUMpslKSbbUXw7lVBcorB3YLArZ0mXL%2FqQRaL9xiKmTNlTim5Xeuw%3D%3D"
    lateinit var recyclerView:RecyclerView
    lateinit var weatherAdapter:WeatherListAdapter
    var coordiList= mutableListOf<Wsss>()
    lateinit var date:String
    lateinit var nickname:String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        date= intent.getStringExtra("today").toString()
        nickname=intent.getStringExtra("nickname").toString()
        Toast.makeText(this,"$nickname 님 환영합니다.",Toast.LENGTH_SHORT).show()
        val title=findViewById<TextView>(R.id.ti)
        title.text=date
        init()
    }

    fun init(){
        findXY()
        recyclerView=findViewById(R.id.recycle)
        recyclerView.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        //Log.i("coordiList.size ",coordiList.size.toString())
        CoroutineScope(Dispatchers.Main).launch {
            connectAPI(coordiList)
        }

    }

    fun findXY(){
        var line:BufferedReader?=null
        var info=""
        try {
            line= BufferedReader(InputStreamReader(resources.openRawResource(R.raw.seoulxy)))
            while(line!=null){
                info=line!!.readLine().toString()

                if(info.equals("end")){
                    break
                }
                var infos=info.split(",")
                coordiList.add(Wsss(infos[0],infos[1],infos[2]))
            }
        }catch (e:IOException){
            Log.e("CSV read error : ",e.toString())
        }
    }

    private suspend fun connectAPI(coordinates: List<Wsss>){
        //coroutine 사용
        //CoroutineScope(Dispatchers.Main).launch {
        val job= Corutines(.IO).async {
            for(i in coordinates.indices){
                val urlBuilder = StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst") /*URL*/ //초단기 실황 조회
                urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8").toString() + "="+serviceKey)/*Service Key-decoding*/
                urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8").toString() + "=" + URLEncoder.encode("1", "UTF-8")) /*페이지번호*/
                urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8").toString() + "=" + URLEncoder.encode("1000", "UTF-8")) /*한 페이지 결과 수*/
                urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8").toString() + "=" + URLEncoder.encode("JSON", "UTF-8")) /*요청자료형식(XML/JSON) Default: XML*/
                urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8").toString() + "=" + URLEncoder.encode(date, "UTF-8")) /*최근 1일만 정보 지원*/
                urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8").toString() + "=" + URLEncoder.encode("0600", "UTF-8")) /*06시 발표(정시단위) */
                urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8").toString() + "=" + URLEncoder.encode(coordinates[i].x, "UTF-8")) /*예보지점의 X 좌표값*/
                urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8").toString() + "=" + URLEncoder.encode(coordinates[i].y, "UTF-8")) /*예보지점의 Y 좌표값*/
                //대방동(59,125) -> 엑셀 확인

                Log.i("urlBuilder.toString ",urlBuilder.toString())

                val url = URL(urlBuilder.toString())
                val conn: HttpURLConnection = url.openConnection() as HttpURLConnection //error
                conn.setRequestMethod("GET")
                conn.setRequestProperty("Content-type", "application/json")
                System.out.println("Response code: " + conn.getResponseCode())
                val rd: BufferedReader
                if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                    rd = BufferedReader(InputStreamReader(conn.getInputStream()))
                } else {
                    rd = BufferedReader(InputStreamReader(conn.getErrorStream()))
                }
                val sb = StringBuilder()
                var line: String?
                while (rd.readLine().also { line = it } != null) {
                    sb.append(line+"\n")
                }
                rd.close()
                conn.disconnect()

                //baseDate : 실황날짜, category : 종류(온도,습도,시간당 강수량), nx : x좌표, ny : y좌표, obsrValue : 해당 코드 값
                var category=""
                var temperature=""
                var humidity=""
                var rainfall=""


                //string to JSON
                val root=JSONObject(sb.toString())
                val response=root.getJSONObject("response").getJSONObject("body").getJSONObject("items")
                val item=response.getJSONArray("item")

                for(i in 0 until item.length()){
                    val jObject=item.getJSONObject(i)
                    category=jObject.getString("category")
                    when(category){
                        "T1H"->{ temperature=jObject.getString("obsrValue")+"℃" }
                        "REH"->{ humidity=jObject.getString("obsrValue")+"%" }
                        "RN1"->{ rainfall=jObject.getString("obsrValue")+"mm" }
                    }
                }

                weatherList.add(Weather(coordinates[i].place,temperature,humidity,rainfall,coordinates[i].x,coordinates[i].y))
                //Log.i("weather check ",coordinates[i].place+" $temperature $humidity $rainfall "+coordinates[i].x+" "+coordinates[i].y)
                //println(sb.toString())

            }

        }

        CoroutineScope(Dispatchers.Main).launch {
            job.await()
            //Log.i("weatherList size ",weatherList.size.toString())

            weatherAdapter= WeatherListAdapter(weatherList)
            recyclerView.adapter=weatherAdapter
        }



    }

    }
}