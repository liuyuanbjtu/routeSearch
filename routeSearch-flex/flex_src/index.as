import mx.collections.ArrayCollection;
import mx.rpc.events.ResultEvent;
import mx.controls.Alert;

// 定义 地图url
[Bindable]
public var mapXML:XML;
[Bindable]
public var basemap:String; //底层地图
[Bindable]
public var stationLayer:String;//车站图层
[Bindable]
public var lineLayer:String; // 查询地图
[Bindable]
public var subwayMapGray:String;//可达性地图
[Bindable]
public var queryStationLayer:String;//可达性查询车站地图URL
[Bindable]
public var queryLineLayer:String;//可达性线路查询地图URL
[Bindable]
public var allStationArr:ArrayCollection = new ArrayCollection();//所有车站集合
public var line1StaArr:ArrayCollection = new ArrayCollection();//1号线车站集合
public var line2StaArr:ArrayCollection = new ArrayCollection();//2号线车站集合
public var line4StaArr:ArrayCollection = new ArrayCollection();//3号线车站集合
public var line5StaArr:ArrayCollection = new ArrayCollection();//4号线车站集合
public var line6StaArr:ArrayCollection = new ArrayCollection();//5号线车站集合
public var line8StaArr:ArrayCollection = new ArrayCollection();//8号线车站集合
public var line9StaArr:ArrayCollection = new ArrayCollection();//三号线北延段线车站集合
public var line10StaArr:ArrayCollection = new ArrayCollection();//AMF线车站集合
public var line13StaArr:ArrayCollection = new ArrayCollection();//广佛线车站集合
public var line14StaArr:ArrayCollection = new ArrayCollection();
public var line15StaArr:ArrayCollection = new ArrayCollection();
public var line94StaArr:ArrayCollection = new ArrayCollection();
public var line95StaArr:ArrayCollection = new ArrayCollection();
public var line96StaArr:ArrayCollection = new ArrayCollection();
public var line97StaArr:ArrayCollection = new ArrayCollection();
public var line98StaArr:ArrayCollection = new ArrayCollection();
[Bindable]
public var stationsArr:ArrayCollection = new ArrayCollection();

private function init():void
{
	loadMapUrlXml();
}

//  加载地图URL配置文件
private function loadMapUrlXml():void
{
	//加载地图配置文件
	var mapUrl:String="mapUrlXml.xml";
	var mapUl:URLLoader=new URLLoader();
	mapUl.load(new URLRequest(mapUrl));
	mapUl.addEventListener(Event.COMPLETE, loadHandler);
}

// 加载配置文件所需的监听
private function loadHandler(event:Event):void
{
	mapXML = XML(event.target.data);
	basemap = mapXML.child("subwayBaseMap").@url;
	lineLayer = mapXML.child("lineLayer").@url;
	stationLayer = mapXML.child("stationLayer").@url;
	
	queryAllStations();
}

private function queryAllStations():void
{
	RouteSearchService.addEventListener(ResultEvent.RESULT, getStationsArr);
	RouteSearchService.queryAllStations();
	
//	RouteSearchService.addEventListener(ResultEvent.RESULT, hello);
//	RouteSearchService.hello();
}

//private function hello(event:ResultEvent) : void {
//	
//	RouteSearchService.removeEventListener(ResultEvent.RESULT, hello);
//	var str:String = event.result as String;
//	
//	Alert.show(str, "java return.");
//	
//}
//获取各线路车站和全部线路
private function getStationsArr(event:ResultEvent):void
{
	RouteSearchService.removeEventListener(ResultEvent.RESULT, getStationsArr);
	var arr:Array = event.result as Array;
	if (arr.length > 0)
	{
		stationsArr.addItemAt(arr[0], 0);
		stationsArr.addItemAt(arr[1],1);
		stationsArr.addItemAt(arr[2],2);
		stationsArr.addItemAt(arr[3],3);
		stationsArr.addItemAt(arr[4],4);
		stationsArr.addItemAt(arr[5],5);
		//stationsArr.addItemAt(new ArrayCollection(),6);
		//stationsArr.addItemAt(new ArrayCollection(),7);
		stationsArr.addItemAt(arr[6],6);
		stationsArr.addItemAt(arr[7],7);
		stationsArr.addItemAt(arr[8],8);
		stationsArr.addItemAt(arr[9],9);
		stationsArr.addItemAt(arr[10],10);
		stationsArr.addItemAt(arr[11],11);
		stationsArr.addItemAt(arr[15],12);
		stationsArr.addItemAt(arr[12],13);
		stationsArr.addItemAt(arr[13],14);
		stationsArr.addItemAt(arr[16],15);
		stationsArr.addItemAt(arr[14],16);
		
		//stationsArr.addItemAt(arr[6],8);
		//stationsArr.addItemAt(arr[7],9);
		//stationsArr.addItemAt(arr[8],10);
		//stationsArr.addItemAt(arr[9],11);
		
		allStationArr=stationsArr.getItemAt(0) as ArrayCollection;
	}
	/*
	allStationArr = arr[0];
	line1StaArr = arr[1];
	line2StaArr = arr[2];
	line3StaArr = arr[3];
	line4StaArr = arr[4];
	line5StaArr = arr[5];
	line8StaArr = arr[6];
	lineSbStaArr = arr[7];
	lineAmfStaArr = arr[8];
	lineGfStaArr = arr[9];
	*/
}