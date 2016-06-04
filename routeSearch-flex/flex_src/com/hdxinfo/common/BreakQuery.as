package com.hdxinfo.common
{
	import flash.events.Event;
	import flash.events.EventDispatcher;
	import flash.events.IEventDispatcher;
	
	import mx.collections.ArrayCollection;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.RemoteObject;

	public class BreakQuery extends EventDispatcher
	{
		private var findTheWay:RemoteObject;
		public var breakArr:ArrayCollection = new ArrayCollection;
		public var breakTongDaoArr:ArrayCollection = new ArrayCollection;
		public var influnceS:ArrayCollection = new ArrayCollection;
		
		public function BreakQuery(target:IEventDispatcher=null)
		{
			super(target);
			findTheWay = new RemoteObject();
			findTheWay.destination = "FindTheWay";
		}
		
		public function queryBreak(queryTime:String):void
		{
			findTheWay.addEventListener(ResultEvent.RESULT, getBreakAndInflunceStation);
			findTheWay.queryBreak(queryTime);
		}

		//获取中断区间和受影响车站
		private function getBreakAndInflunceStation(event:ResultEvent):void
		{
			findTheWay.removeEventListener(ResultEvent.RESULT, getBreakAndInflunceStation);
			var resultArr:Array=event.result as Array;
			var breakAndTongDaoArr:ArrayCollection=resultArr[0];
			for (var i:int=0; i < breakAndTongDaoArr.length; i++)
			{
				var obj:Object=breakAndTongDaoArr[i];
				var obj1:Object=obj.SStation;
				var obj2:Object=obj.EStation;
				if (obj1.stationName == obj2.stationName)
				{
					breakTongDaoArr.addItem(obj);
				}
				else
				{
					breakArr.addItem(obj);
				}
			}

			influnceS=resultArr[1];
			dispatchEvent(new Event("break data is ready"));
		}
	}
}