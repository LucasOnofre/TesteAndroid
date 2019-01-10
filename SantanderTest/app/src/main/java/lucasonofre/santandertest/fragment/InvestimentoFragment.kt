package lucasonofre.santandertest.fragment


import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_investimento.*
import lucasonofre.santandertest.R
import lucasonofre.santandertest.adapter.InfoAdapter
import lucasonofre.santandertest.adapter.InfoDownAdapter
import lucasonofre.santandertest.adapter.InfoMoreAdapter
import lucasonofre.santandertest.model.*
import lucasonofre.santandertest.request.RequestItens
import retrofit2.Call
import retrofit2.Response
import android.view.ViewGroup.MarginLayoutParams


class InvestimentoFragment : Fragment() {

    private var screenResult:Screen?                        = null
    private var arrayMoreInfo:Array<YieldListItem>?         = null
    private var moreInfolist: RecyclerView?                 = null
    private var infoList: RecyclerView?                     = null
    private var infoDownList: RecyclerView?                 = null
    private var arrayListMoreInfo:ArrayList<YieldListItem>? = null
    private var arrayListInfo:ArrayList<Info>?              = null
    private var arrayListDownInfo:ArrayList<DownInfo>?      = null
    private var layout      : ConstraintLayout?             = null
    private var btnInvestir : Button?                       = null
    private var indicadorRisco:ImageView?                   = null
    private var layoutRiscos:ConstraintLayout?              = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_investimento, container, false)

        setViews(view)
        requestFund()

        return view
    }

    /**
     * Recupera as listas e as views do layout e da progress view
     */

    private fun setViews(view: View) {
        infoList        = view.findViewById(R.id.list_info)
        moreInfolist    = view.findViewById(R.id.list_moreInfo)
        infoDownList    = view.findViewById(R.id.list_down_info)
        layout          = view.findViewById(R.id.container)
        btnInvestir     = view.findViewById(R.id.invest_btn)
        indicadorRisco  = view.findViewById(R.id.invest_risk_indicator)
        layoutRiscos    = view.findViewById(R.id.layout_view_risks)

    }

    /**
     * Configura o indicador de risco conforme a chamada
     */

    private fun setRiskIndicator(risk: Int?) {

        var indicators  = arrayListOf(invest_risk_1, invest_risk_2, invest_risk_3, invest_risk_4, invest_risk_5)
        var larguraLayoutTamanhos = layoutRiscos?.width?.minus(indicadorRisco!!.width)?.div(indicators.size)
        var posicaoSeta           = larguraLayoutTamanhos?.times(risk!!)?.minus(indicadorRisco!!.width)


        for (item in indicators) {
            if (risk == indicators.indexOf(item) + 1) {

                item.layoutParams.height            = item.layoutParams.height.times(2).minus(item.layoutParams.height.div(2))
                val marginParams = indicadorRisco?.layoutParams as MarginLayoutParams
                marginParams.setMargins(posicaoSeta!!, 0, 0, 0)


            }
        }
    }

    /**
     * Insere as informações vindas da chamada em seu respectivo campo
     */

    private fun setUpInfo(screenResult: Screen?) {

        invest_title.text       = screenResult?.screen?.title
        invest_investName.text  = screenResult?.screen?.fundName
        invest_whatIs.text      = screenResult?.screen?.whatIs
        contact_item_text.text  = screenResult?.screen?.definition
        invest_riskTitle.text   = screenResult?.screen?.riskTitle
        invest_infoTitle.text   = screenResult?.screen?.infoTitle

        setRiskIndicator(screenResult?.screen?.risk?.toInt())
        setListMoreInfo(screenResult?.screen?.moreInfo)
        setListInfo(screenResult)
        setListDownInfo(screenResult)

        btnInvestir?.setOnClickListener { Toast.makeText(context,"Investimento realizado",Toast.LENGTH_SHORT).show() }


    }

    /**
     * Trata e insere as informaçõs da lista moreInfo na tela
     */
    private fun setListMoreInfo(moreInfo: MoreInfo?) {

        arrayMoreInfo = arrayOf(

            YieldListItem(moreInfo?.month,"No Mês"),
            YieldListItem(moreInfo?.year,"No Ano"),
            YieldListItem(moreInfo?.the12Months,"12 meses"))


        arrayListMoreInfo           = arrayMoreInfo?.toCollection(ArrayList())
        moreInfolist?.layoutManager = LinearLayoutManager(context)
        moreInfolist?.adapter       = InfoMoreAdapter(activity!!,arrayListMoreInfo!!)

    }

    /**
     * Trata e insere as informaçõs da lista info na tela
     */

    private fun setListInfo(screenResult: Screen?) {
        arrayListInfo           = screenResult?.screen?.info
        infoList?.layoutManager = LinearLayoutManager(context)
        infoList?.adapter       = InfoAdapter(activity!!, arrayListInfo!!)
    }

    /**
     * Trata e insere as informaçõs da lista DownInfo na tela
     */

    private fun setListDownInfo(screenResult: Screen?) {
        arrayListDownInfo           = screenResult?.screen?.downInfo
        infoDownList?.layoutManager = LinearLayoutManager(context)
        infoDownList?.adapter       = InfoDownAdapter(activity!!, arrayListDownInfo!!)
    }

    /**
     * Chamada que recebe a requisição da tela de contatos
     */
    private fun requestFund() {

        activity?.let {

            RequestItens(it).getFund().enqueue(object:retrofit2.Callback<Screen>{
                override fun onFailure(call: Call<Screen>, t: Throwable) {
                    Log.i("Error", t.message)
                }

                override fun onResponse(call: Call<Screen>, response: Response<Screen>) {

                    screenResult = response.body()
                    setUpInfo(screenResult)
                    setRiskIndicator(screenResult?.screen?.risk?.toInt())


                }
            })
        }
    }
}
