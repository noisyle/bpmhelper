<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="element-ui/index.css">
<style>
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
          <el-form :inline="true" @submit.native.prevent="search">
            <el-form-item label="流程名称">
              <el-input v-model="table.filter"></el-input>
            </el-form-item>
          </el-form>
          <el-table border stripe class="model-table" v-loading="table.loading" :data="table.list" @row-click="rowClick">
            <el-table-column prop="id" label="ID" width="180"></el-table-column>
            <el-table-column prop="name" label="名称"></el-table-column>
          </el-table>
          <el-pagination background layout="prev, pager, next, total" :total="table.total" :current-page="table.page" 
              @current-change="pageChange" @prev-click="pageChange" @next-click="pageChange">
          </el-pagination>
        </el-aside>
        <el-main style="margin-left: 500px;">
          <el-form ref="form" :model="form" label-width="120px" v-loading="form.loading">
            <el-form-item label="ID">
              <el-input v-model="form.id" readonly></el-input>
            </el-form-item>
            <el-form-item label="名称">
              <el-input v-model="form.name" readonly></el-input>
            </el-form-item>
            <el-form-item label="json">
              <el-input type="textarea" v-model="form.json" :autosize="{ minRows: 2, maxRows: 20 }"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="onSave">保存</el-button>
            </el-form-item>
          </el-form>
        </el-main>
      </el-container>
    </el-container>
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
				table : {
					loading: false,
					filter: '',
					list : [],
					total : 0,
					page : 1
				},
				form : {
					loading: false,
					id: '',
                    name : '',
                    json : ''
                }
			}
		},
		mounted : function() {
			var that = this
			that.table.loading = true
			axios.get('/bizmodel/page/' + that.table.page)
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
              axios.get('/bizmodel/page/' + page + (filter ? '?filter=' + filter : ''))
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
          pageChange : function(page){
              this.loadTable(this.table.filter, page)
          },
          search : function(){
        	  this.loadTable(this.table.filter, this.table.page)
          },
          rowClick : function(row, event, column){
        	  var that = this
              that.form.loading = true
              axios.get('/bizmodel/' + row.id)
                .then(function (res) {
                  that.form.id = res.data.id
                  that.form.name = res.data.name
                  that.form.json = res.data.json
                  that.form.loading = false
                })
                .catch(function (err) {
                  console.log(err)
                  that.form.loading = false
                })
          },
          onSave : function(){
        	  var that = this
              this.$confirm('此操作将修改流程发布, 是否继续?', '提示', {
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
                      formData.append('json', that.form.json);
                      
                      axios.put('/bizmodel/' + that.form.id, formData)
                        .then(function (res) {
                          loading.close()
                          that.$alert('保存成功', '提示', {type: 'success'})
                        })
                        .catch(function (err) {
                          console.log(err)
                          loading.close()
                        })
                })
          }
        }
	});
</script>
</html>