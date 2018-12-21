<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="element-ui/index.css">
<style>
.dgrm,
.model-table td {
  cursor: pointer;
}
</style>
</head>
<body>
  <div id="app">
    <el-container>
      <el-header></el-header>
      <el-container>
        <el-aside style="position: fixed; width: 500px;">
          <el-form :inline="true">
            <el-form-item>
              <el-input v-model="table.filter.name" placeholder="流程名称" @keyup.enter.native="search"></el-input>
            </el-form-item>
            <el-form-item>
              <el-select v-model="table.filter.category" placeholder="流程类型" clearable="true" @change="search">
                <el-option v-for="category in categories" :key="category.id" :label="category.name" :value="category.id"></el-option>
              </el-select>
            </el-form-item>
          </el-form>
          <el-table border stripe class="model-table" v-loading="table.loading" :data="table.list" @row-click="rowClick">
            <el-table-column prop="id" label="model ID" width="100"></el-table-column>
            <el-table-column prop="name" label="名称"></el-table-column>
            <el-table-column prop="version" label="版本" width="80"></el-table-column>
          </el-table>
          <el-pagination background layout="prev, pager, next, total" :total="table.total" :current-page="table.page" 
              @current-change="pageChange" @prev-click="pageChange" @next-click="pageChange">
          </el-pagination>
        </el-aside>
        <el-main style="margin-left: 500px;">
          <el-form ref="form" :model="form" label-width="120px" v-loading="form.loading">
            <el-form-item label="拓扑">
              <img v-if="form.deploymentId" class="dgrm" v-bind:src="'/procmodel/' + form.deploymentId + '_' + form.dgrmResourceName"
                style="max-width: 100%;" @click="dgrmVisible = true" />
            </el-form-item>
            <el-form-item label="model ID">
              <span>{{ form.id }}</span>
            </el-form-item>
            <el-form-item label="名称">
              <span>{{ form.name }}</span>
            </el-form-item>
            <el-form-item label="条件分支">
              <el-table border :data="form.conditions" size="mini">
                <el-table-column label="条件">
                  <template slot-scope="scope">
                    <el-input size="mini" v-model="scope.row.properties.conditionsequenceflow"></el-input>
                  </template>
                </el-table-column>
                <el-table-column label="变量名称">
                  <template slot-scope="scope">
                    <el-input size="mini" v-model="form.variables[scope.$index].name"></el-input>
                  </template>
                </el-table-column>
                <el-table-column label="变量值">
                  <template slot-scope="scope">
                    <el-input size="mini" v-model="form.variables[scope.$index].value"></el-input>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="100">
                  <template slot-scope="scope">
                    <el-button size="mini" type="danger"
                      @click="convert(scope.$index, scope.row)">转换</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-form-item>
            <el-form-item label="流程model">
              <el-collapse>
                <el-collapse-item>
                  <el-input type="textarea" v-model="form.bytes" :autosize="{ minRows: 2, maxRows: 10 }"></el-input>
                </el-collapse-item>
              </el-collapse>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="onSave">保存</el-button>
            </el-form-item>
          </el-form>
        </el-main>
      </el-container>
    </el-container>
    <el-dialog :visible.sync="dgrmVisible" fullscreen="true">
      <img v-if="form.deploymentId" v-bind:src="'/procmodel/' + form.deploymentId + '_' + form.dgrmResourceName" style="display: block; margin: 0 auto;"/>
    </el-dialog>
  </div>
</body>
<script src="jquery.slim.min.js"></script>
<script src="vuejs/vue.min.js"></script>
<script src="element-ui/index.js"></script>
<script src="axios/axios.min.js"></script>
<script>
	var vm = new Vue({
		el : '#app',
		data : function() {
			return {
				categories : [],
				table : {
					loading: false,
					filter: {name: '', category: null},
					list : [],
					total : 0,
					page : 1
				},
				form : {
					loading: false,
					id: '',
                    name : '',
                    version : '',
                    deploymentId : '',
                    resourceName : '',
                    dgrmResourceName : '',
                    bytes : '',
                    model: null,
                    conditions: [],
                    variables: []
                },
                dgrmVisible: false
			}
		},
		mounted : function() {
			var that = this
			that.table.loading = true
			axios.get('/categories')
			  .then(function (res) {
			    res.data.forEach(cat => that.categories.push(cat))
			  })
			  .catch(function (err) {
			    console.log(err)
			  })
			axios.get('/procmodel/page/' + that.table.page)
			  .then(function (res) {
			    that.table.list = res.data.list
			    that.table.total = res.data.total
			    that.table.page = res.data.pageNum
	            that.table.loading = false
			  })
			  .catch(function (err) {
			    console.log(err)
                that.table.loading = false
			  })
		},
		methods: {
          loadTable : function(filter, page){
              var that = this
              that.table.loading = true
              axios.get('/procmodel/page/' + page, {params: filter})
                .then(function (res) {
                  that.table.list = res.data.list
                  that.table.total = res.data.total
                  that.table.page = res.data.pageNum
                  that.table.loading = false
                  
                  if(res.data.list) {
                	  that.rowClick(res.data.list[0])
                  }
                })
                .catch(function (err) {
                  console.log(err)
                  that.table.loading = false
                })
          },
          pageChange : function(page){
              this.loadTable(this.table.filter, page)
          },
          search : function(){
        	  this.loadTable(this.table.filter, this.table.page)
          },
          rowClick : function(row, event, column){
        	  var that = this
              that.form.loading = true
              axios.get('/procmodel/' + row.id)
                .then(function (res) {
                  that.form.id = res.data.id
                  that.form.name = res.data.name
                  that.form.version = res.data.version
                  that.form.deploymentId = res.data.deploymentId
                  that.form.resourceName = res.data.resourceName
                  that.form.dgrmResourceName = res.data.dgrmResourceName
                  that.form.bytes = res.data.bytesString
                  that.form.model = JSON.parse(that.form.bytes)
                  that.form.loading = false
                  
                  that.form.conditions.splice(0, that.form.conditions.length)
                  that.form.variables.splice(0, that.form.variables.length)
                  var model = that.form.model
                  model.childShapes
                    .filter(shape => shape.stencil.id==='SequenceFlow' && shape.properties.conditionsequenceflow)
                    .forEach(shape => {
                  		that.form.conditions.push(shape)
                        that.form.variables.push({
                        	name: '',
                        	value: '',
                        	modelId: that.form.id,
                        	resourceId: shape.resourceId
                        })
                    })
                })
                .catch(function (err) {
                  console.log(err)
                  that.form.loading = false
                })
          },
          convert : function(index, row){
        	  var condition = row.properties.conditionsequenceflow
        	  var varValue = condition.match(/'.*?'/g).map(m => m.replace(/'/g, '')).join(',')
        	  this.form.variables[index].value = varValue
          },
          onSave : function(){
        	  var that = this
              this.$confirm('此操作将修改流程定义, 是否继续?', '提示', {
                  confirmButtonText: '确定',
                  cancelButtonText: '取消',
                  type: 'warning'
                }).then(() => {
                    const loading = this.$loading({
                      lock: true,
                      text: '处理中',
                      spinner: 'el-icon-loading',
                      background: 'rgba(0, 0, 0, 0.7)'
                    });
                    
                    var formData = new FormData();
                    formData.append('bytes', JSON.stringify(that.form.model));
                    formData.append('variables', JSON.stringify(that.form.variables));
                    
                    axios.put('/procmodel/' + that.form.id, formData)
                      .then(function (res) {
                        that.$alert('保存成功', '提示', {type: 'success'})
                        loading.close()
                      })
                      .catch(function (err) {
                        console.log(err)
                        that.$alert(err.message, '提示', {type: 'error'})
                        loading.close()
                      })
                })
          }
        }
	});
</script>
</html>