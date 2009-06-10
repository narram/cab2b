/**
 * 	Generated by mxmlc 2.0
 *
 *	Package:	Components
 *	Class: 		AmbiguityPanel
 *	Source: 	C:\Eclipse\workspace\catissuecore_Merge\flexclient\dag\Components\AmbiguityPanel.mxml
 *	Template: 	flex2/compiler/mxml/gen/ClassDef.vm
 *	Time: 		2007.09.21 11:53:38 GMT+05:30
 */

package Components
{

import Components.AmbiguityPanel_inlineComponent1;
import flash.accessibility.*;
import flash.debugger.*;
import flash.display.*;
import flash.errors.*;
import flash.events.*;
import flash.events.MouseEvent;
import flash.external.*;
import flash.filters.*;
import flash.geom.*;
import flash.media.*;
import flash.net.*;
import flash.printing.*;
import flash.profiler.*;
import flash.system.*;
import flash.text.*;
import flash.ui.*;
import flash.utils.*;
import flash.xml.*;
import mx.binding.*;
import mx.containers.ControlBar;
import mx.containers.Panel;
import mx.controls.Button;
import mx.controls.DataGrid;
import mx.controls.Spacer;
import mx.controls.dataGridClasses.DataGridColumn;
import mx.core.ClassFactory;
import mx.core.DeferredInstanceFromClass;
import mx.core.DeferredInstanceFromFunction;
import mx.core.IDeferredInstance;
import mx.core.IFactory;
import mx.core.IPropertyChangeNotifier;
import mx.core.UIComponentDescriptor;
import mx.core.mx_internal;
import mx.events.FlexEvent;
import mx.styles.*;



public class AmbiguityPanel
	extends mx.containers.Panel
{

	[Bindable]
/**
 * @private
 **/
	public var _DataGrid1 : mx.controls.DataGrid;




private var _documentDescriptor_ : mx.core.UIComponentDescriptor = 
new mx.core.UIComponentDescriptor({
  type: mx.containers.Panel
  ,
  propertiesFactory: function():Object { return {
    width: 550,
    height: 500,
    childDescriptors: [
      new mx.core.UIComponentDescriptor({
        type: mx.controls.DataGrid
        ,
        id: "_DataGrid1"
        ,
        propertiesFactory: function():Object { return {
          name: "pathGrid",
          width: 550,
          x: 0,
          height: 500,
          y: 0,
          verticalScrollPolicy: "off",
          horizontalScrollPolicy: "off",
          columns: [_DataGridColumn1_c(), _DataGridColumn2_c()]
        }}
      })
    ,
      new mx.core.UIComponentDescriptor({
        type: mx.containers.ControlBar
        ,
        propertiesFactory: function():Object { return {
          name: "controlBar",
          childDescriptors: [
            new mx.core.UIComponentDescriptor({
              type: mx.controls.Spacer
              ,
              propertiesFactory: function():Object { return {
                percentWidth: 100.0
              }}
            })
          ,
            new mx.core.UIComponentDescriptor({
              type: mx.controls.Button
              ,
              events: {
                click: "___Button1_click"
              }
              ,
              propertiesFactory: function():Object { return {
                name: "OK",
                label: "OK"
              }}
            })
          ,
            new mx.core.UIComponentDescriptor({
              type: mx.controls.Button
              ,
              events: {
                click: "___Button2_click"
              }
              ,
              propertiesFactory: function():Object { return {
                name: "Cancel",
                label: "Cancel"
              }}
            })
          ]
        }}
      })
    ]
  }}
})

    /**
     * @private
     **/
	public function AmbiguityPanel()
	{
		super();

		mx_internal::_document = this;

		//	our style settings



		//	properties
		this.layout = "absolute";
		this.width = 550;
		this.height = 500;

		//	events
		this.addEventListener("creationComplete", ___Panel1_creationComplete);

	}

    /**
     * @private
     **/
	override public function initialize():void
	{
 		mx_internal::setDocumentDescriptor(_documentDescriptor_);

		//	binding mgmt
		_AmbiguityPanel_bindingsSetup();

		var target:AmbiguityPanel = this;

		if (_watcherSetupUtil == null)
		{
			var watcherSetupUtilClass:Object = getDefinitionByName("_Components_AmbiguityPanelWatcherSetupUtil");
			watcherSetupUtilClass["init"](null);
		}

		_watcherSetupUtil.setup(this,
					function(propertyName:String):* { return target[propertyName]; },
					_bindings,
					_watchers);


		super.initialize();
	}


		import mx.controls.CheckBox;
		import mx.binding.utils.BindingUtils;
		import mx.collections.ArrayCollection;
		import mx.managers.PopUpManager;
	    import mx.events.CollectionEvent;
	    import Components.IDAGPath;
	    import mx.controls.Alert;
		import mx.managers.PopUpManager;
		import mx.rpc.events.ResultEvent;

	    [Bindable]
		public var pathList:ArrayCollection=new ArrayCollection();;
	    [Bindable]
		public var selectedList:ArrayCollection= null;
		
		public var nodeList:ArrayCollection =null;						
		public var dagPath: IDAGPath = null;
		
		private function init():void
        {
        	 //Intialising ambiguity panel
        	selectedList = new ArrayCollection()
        	pathList.addEventListener(CollectionEvent.COLLECTION_CHANGE, handleDataProviderChange);
        }
       
		public  function closePopUp(event:MouseEvent):void {
				var buttonStr:Array=event.currentTarget.toString().split(".");
				var index:int=(buttonStr.length-1)
	   	 	 	PopUpManager.removePopUp(this);
				if(buttonStr[index]=="OK")
				{
					this.parentApplication.rpcService.removeEventListener(ResultEvent.RESULT,this.parentApplication.getPathHandler,false);
					this.parentApplication.rpcService.addEventListener("result",this.parentApplication.linkNodesHandler);
					if(nodeList!=null&&selectedList!=null)
					{
						this.parentApplication.rpcService.linkNodes(nodeList,selectedList);
					}
				}
				else
				{
					selectedList=null;
					pathList=null;
					this.parentApplication.cancelHandler(nodeList);
				}
						
		}

	private function handleDataProviderChange(event:CollectionEvent):void
        {
		
		try{
				var len:int = pathList.length;
				for (var i:int=0;i<len;i++)
				{
					dagPath = pathList.getItemAt(i) as IDAGPath;
					if (dagPath.isSelected)
					{
						selectedList.addItem(dagPath);
					}
				}
			}catch(error:Error)
			{
				Alert.show("Error :"+error.message);
			
			}
			
             
         }
		
	



    //	supporting function definitions for properties, events, styles, effects
/**
 * @private
 **/
public function ___Panel1_creationComplete(event:mx.events.FlexEvent):void
{
	init()
}

private function _DataGridColumn1_c() : mx.controls.dataGridClasses.DataGridColumn
{
	var temp : mx.controls.dataGridClasses.DataGridColumn = new mx.controls.dataGridClasses.DataGridColumn();
	temp.headerText = "Paths";
	temp.dataField = "name";
	temp.dataTipField = "name";
	temp.showDataTips = true;
	temp.width = 450;
	temp.wordWrap = true;
	return temp;
}

private function _DataGridColumn2_c() : mx.controls.dataGridClasses.DataGridColumn
{
	var temp : mx.controls.dataGridClasses.DataGridColumn = new mx.controls.dataGridClasses.DataGridColumn();
	temp.headerText = "Select";
	temp.dataField = "isSelected";
	temp.width = 100;
	temp.itemRenderer = _ClassFactory1_c();
	return temp;
}

private function _ClassFactory1_c() : mx.core.ClassFactory
{
	var temp : mx.core.ClassFactory = new mx.core.ClassFactory();
	temp.generator = Components.AmbiguityPanel_inlineComponent1;
	temp.properties = {outerDocument: this};
	return temp;
}

/**
 * @private
 **/
public function ___Button1_click(event:flash.events.MouseEvent):void
{
	closePopUp(event)
}

/**
 * @private
 **/
public function ___Button2_click(event:flash.events.MouseEvent):void
{
	closePopUp(event)
}


	//	binding mgmt
    private var _bindings:Array;
    private var _watchers:Array;
    private function _AmbiguityPanel_bindingsSetup():void
    {
        if (!_bindings)
        {
            _bindings = [];
        }

        if (!_watchers)
        {
            _watchers = [];
        }

        var binding:Binding;

        binding = new mx.binding.Binding(this,
            function():Object
            {
                return (pathList);
            },
            function(_sourceFunctionReturnValue:Object):void
            {
				
                _DataGrid1.dataProvider = _sourceFunctionReturnValue;
            },
            "_DataGrid1.dataProvider");
        _bindings[0] = binding;
    }

    private function _AmbiguityPanel_bindingExprs():void
    {
        var destination:*;
		[Binding(id='0')]
		destination = (pathList);
    }

    /**
     * @private
     **/
    public static function set watcherSetupUtil(watcherSetupUtil:IWatcherSetupUtil):void
    {
        (AmbiguityPanel)._watcherSetupUtil = watcherSetupUtil;
    }

    private static var _watcherSetupUtil:IWatcherSetupUtil;





    /**
     * @private
     **/
    public var _bindingsByDestination : Object;
    /**
     * @private
     **/
    public var _bindingsBeginWithWord : Object;

}

}
